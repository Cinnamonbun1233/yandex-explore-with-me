package ewm.server.service.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import ewm.client.StatsClient;
import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import ewm.server.dto.event.*;
import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.exception.category.CategoryNotFoundException;
import ewm.server.exception.event.EventNotFoundException;
import ewm.server.exception.event.IllegalDatesException;
import ewm.server.exception.event.IllegalPublicationException;
import ewm.server.exception.event.UnknownActionException;
import ewm.server.exception.request.IllegalRequestException;
import ewm.server.exception.user.UserNotFoundException;
import ewm.server.mapper.event.EventMapper;
import ewm.server.mapper.event.LocationMapper;
import ewm.server.mapper.request.RequestMapper;
import ewm.server.model.category.Category;
import ewm.server.model.event.Event;
import ewm.server.model.event.EventStatus;
import ewm.server.model.event.Location;
import ewm.server.model.event.QEvent;
import ewm.server.model.request.ParticipationRequest;
import ewm.server.model.request.QParticipationRequest;
import ewm.server.model.request.RequestStatus;
import ewm.server.model.user.User;
import ewm.server.repo.category.CategoryRepo;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.event.LocationRepo;
import ewm.server.repo.request.RequestRepo;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;
    private final LocationRepo locationRepo;
    private final RequestRepo requestRepo;
    private final StatsClient statsClient;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event newEvent = EventMapper.mapDtoToModel(newEventDto);
        validateEventDate(newEvent.getEventDate());
        newEvent.setCategory(getCategory(newEventDto.getCategory()));
        newEvent.setLocation(saveLocation(newEventDto.getLocation()));
        newEvent.setInitiator(getInitiator(userId));
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setEventStatus(EventStatus.PENDING);
        Event savedEvent = eventRepo.save(newEvent);
        return EventMapper.mapModelToFullDto(savedEvent, getViewsFromStats(savedEvent.getId()));
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest) {
        Event toBeUpdated = getEvent(eventId);
        updateEvent(toBeUpdated, updateRequest);
        updateStatusAdmin(toBeUpdated, updateRequest);
        Event savedEvent = eventRepo.save(toBeUpdated);
        return EventMapper.mapModelToFullDto(savedEvent, getViewsFromStats(savedEvent.getId()));
    }

    @Override
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventRequest updateRequest) {
        checkIfUserExists(userId);
        Event toBeUpdated = getEvent(eventId);
        checkIfEventAlreadyPublished(toBeUpdated);
        updateEvent(toBeUpdated, updateRequest);
        updateStatusUser(toBeUpdated, updateRequest);
        Event savedEvent = eventRepo.save(toBeUpdated);
        return EventMapper.mapModelToFullDto(savedEvent, getViewsFromStats(savedEvent.getId()));
    }

    @Override
    public List<EventFullDto> searchEventsAdmin(Optional<Integer[]> users, Optional<String[]> states,
                                                Optional<Integer[]> categories, Optional<String> rangeStart,
                                                Optional<String> rangeEnd, int from, int size) {
        Pageable request = makePageRequest(from, size);
        BooleanExpression searchExp = makeSearchExpAdmin(users, states, categories, rangeStart, rangeEnd);
        return eventRepo.findAll(searchExp, request).stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .map(e -> EventMapper.mapModelToFullDto(e, getViewsFromStats(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllUsersEvents(Long userId, int from, int size) {
        Pageable request = makePageRequest(from, size);
        BooleanExpression byUserId = QEvent.event.initiator.id.eq(userId);
        return eventRepo.findAll(byUserId, request).stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .map(e -> EventMapper.mapModelToShortDto(e, getViewsFromStats(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> searchEventsPublic(Optional<String> text, Optional<Integer[]> categories,
                                                  Optional<Boolean> paid, Optional<String> rangeStart,
                                                  Optional<String> rangeEnd, Boolean onlyAvailable,
                                                  String sort, int from, int size) {
        saveStats("/events");
        Pageable request = makePageRequest(from, size);
        BooleanExpression searchExp = makeSearchExpPublic(text, categories, paid, rangeStart, rangeEnd);
        Comparator<EventShortDto> comparator = makeComparator(sort);
        if (onlyAvailable) {
            return eventRepo.findAll(searchExp, request).stream()
                    .filter(e -> e.getRequests().stream()
                                         .filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED))
                                         .count() < e.getParticipationLimit())
                    .map(e -> EventMapper.mapModelToShortDto(e, getViewsFromStats(e.getId())))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            return eventRepo.findAll(searchExp, request).stream()
                    .map(e -> EventMapper.mapModelToShortDto(e, getViewsFromStats(e.getId())))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEventByIdPublic(Long id) {
        saveStats(String.format("/events/%d", id));
        Event eventFound = eventRepo.findByIdAndEventStatus(id, EventStatus.PUBLISHED)
                .orElseThrow(() -> {
                    throw new EventNotFoundException("Event not found");
                });
        return EventMapper.mapModelToFullDto(eventFound, getViewsFromStats(eventFound.getId()));
    }

    private void saveStats(String uri) {
        statsClient.saveRecord(StatsRequestDto.builder()
                .uri(uri)
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now().format(REQUEST_TIME_FORMAT))
                .build());
    }

    private Integer getViewsFromStats(Long id) {
        StatsResponseDto stats = statsClient.getStats("2000-01-01 00:00:00",
                "2100-01-01 00:00:00",
                new String[]{String.format("/events/%d", id)}, "true").blockFirst();
        return stats == null ? 0 : stats.getHits().intValue();
    }

    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        checkIfUserExists(userId);
        Event eventFound = eventRepo.findById(eventId)
                .orElseThrow(() -> {
                    throw new EventNotFoundException("Event not found");
                });
        return EventMapper.mapModelToFullDto(eventFound, getViewsFromStats(eventFound.getId()));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestByInitiator(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest request) {
        checkIfUserExists(userId);
        RequestStatus status = parseRequestStatus(request.getStatus());
        List<ParticipationRequest> toBeUpdated = makeListOfRequestsToBeUpdated(eventId, request);
        checkIfUpdateRequestIsValid(status, toBeUpdated);
        toBeUpdated.forEach(r -> r.setRequestStatus(status));
        requestRepo.saveAllAndFlush(toBeUpdated);
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestRepo.findAllByRequestStatusAndEvent_Id(RequestStatus.CONFIRMED, eventId)
                        .stream()
                        .map(RequestMapper::mapModelToDto)
                        .collect(Collectors.toList()))
                .rejectedRequests(requestRepo.findAllByRequestStatusAndEvent_Id(RequestStatus.REJECTED, eventId)
                        .stream()
                        .map(RequestMapper::mapModelToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private void checkIfUpdateRequestIsValid(RequestStatus status, List<ParticipationRequest> toBeUpdated) {
        switch (status) {
            case CONFIRMED:
                if (toBeUpdated.stream()
                        .anyMatch(r -> r.getEvent().getParticipationLimit() != 0 &&
                                       r.getEvent().getParticipationLimit() == r.getEvent().getRequests().stream()
                                               .filter(r1 -> r1.getRequestStatus().equals(RequestStatus.CONFIRMED))
                                               .count())) {
                    throw new IllegalRequestException("Participant limit to one of events has been already reached");
                }
                break;
            case REJECTED:
                if (toBeUpdated.stream()
                        .anyMatch(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED))) {
                    throw new IllegalRequestException("One of requests has been already confirmed");
                }
                break;
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequestsToUsersEvent(Long userId, Long eventId) {
        checkIfUserExists(userId);
        Event eventFound = eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event does not exist");
        });
        return eventFound.getRequests().stream().map(RequestMapper::mapModelToDto).collect(Collectors.toList());
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().minusHours(2).isAfter(eventDate) || eventDate.isBefore(LocalDateTime.now())) {
            throw new IllegalDatesException("Event date has to be at least 2 hours after current moment");
        }
    }

    private List<ParticipationRequest> makeListOfRequestsToBeUpdated(Long eventId,
                                                                     EventRequestStatusUpdateRequest request) {
        QParticipationRequest qRequest = QParticipationRequest.participationRequest;
        BooleanExpression exp = qRequest.event.id.eq(eventId)
                .and(qRequest.id.in(request.getRequestIds()));
        return StreamSupport.stream(requestRepo.findAll(exp).spliterator(), false)
                .collect(Collectors.toList());
    }

    private RequestStatus parseRequestStatus(String status) {
        try {
            return RequestStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Status is unknown");
        }
    }

    private Comparator<EventShortDto> makeComparator(String sort) {
        SortType sorting = parseSortType(sort);
        if (sorting.equals(SortType.VIEWS)) {
            return Comparator.comparing(EventShortDto::getViews);
        }
        return Comparator.comparing(EventShortDto::getEventDate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BooleanExpression makeSearchExpAdmin(Optional<Integer[]> users, Optional<String[]> states, Optional<Integer[]> categories, Optional<String> rangeStart, Optional<String> rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        users.ifPresent(userIds -> builder.and(qEvent.initiator.id.in(userIds)));
        states.ifPresent(stateStrings -> builder.and(qEvent.eventStatus.in(Arrays.stream(states.get())
                .map(this::parseEventStatus)
                .toArray(EventStatus[]::new))));
        categories.ifPresent(categoryIds -> builder.and(qEvent.category.id.in(categoryIds)));
        rangeStart.ifPresent(start -> builder.and(qEvent.eventDate.after(parseDateTime(start))));
        rangeEnd.ifPresent(end -> builder.and(qEvent.eventDate.before(parseDateTime(end))));
        return builder.getValue() != null ? Expressions.asBoolean(builder.getValue()) : qEvent.isNotNull();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BooleanExpression makeSearchExpPublic(Optional<String> text, Optional<Integer[]> categories, Optional<Boolean> paid, Optional<String> rangeStart, Optional<String> rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.eventStatus.eq(EventStatus.PUBLISHED));
        text.ifPresent(str -> {
            BooleanExpression annotationContainsText = qEvent.annotation.likeIgnoreCase(str);
            BooleanExpression descriptionContainsText = qEvent.description.likeIgnoreCase(str);
            builder.and(annotationContainsText.or(descriptionContainsText));
        });
        categories.ifPresent(categoryIds -> builder.and(qEvent.category.id.in(categoryIds)));
        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            validateSearchDates(rangeStart.get(), rangeEnd.get());
        }
        rangeStart.ifPresent(start -> builder.and(qEvent.eventDate.after(parseDateTime(start))));
        rangeEnd.ifPresent(end -> builder.and(qEvent.eventDate.before(parseDateTime(end))));
        if (rangeStart.isEmpty() || rangeEnd.isEmpty()) {
            builder.and(qEvent.eventDate.after(LocalDateTime.now()));
        }
        paid.ifPresent(bool -> builder.and(qEvent.paid.eq(bool)));
        return Expressions.asBoolean(builder.getValue());
    }

    private void validateSearchDates(String rangeStart, String rangeEnd) {
        if (parseDateTime(rangeStart).isAfter(parseDateTime(rangeEnd))) {
            throw new IllegalDatesException("Range start has to be after range end");
        }
    }

    private Location saveLocation(LocationDto locationDto) {
        return locationRepo.save(LocationMapper.mapDtoToModel(locationDto));
    }

    private Category getCategory(Integer categoryId) {
        return categoryRepo.findById(categoryId.longValue()).orElseThrow(() -> {
            throw new CategoryNotFoundException("Category does not exist");
        });
    }

    private User getInitiator(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        });
    }

    private Event getEvent(Long eventId) {
        return eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event does not exist");
        });
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    private void updateEvent(Event toBeUpdated, UpdateEventRequest updateRequest) {
        updateDescription(toBeUpdated, updateRequest);
        updateAnnotation(toBeUpdated, updateRequest);
        updateParticipantLimit(toBeUpdated, updateRequest);
        updateTitle(toBeUpdated, updateRequest);
        updatePaid(toBeUpdated, updateRequest);
        updateCategory(toBeUpdated, updateRequest);
        updateEventDate(toBeUpdated, updateRequest);
        updateLocation(toBeUpdated, updateRequest);
    }

    private void updateStatusAdmin(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getStateAction() != null) {
            StateAdminAction action = parseActionAdmin(updateRequest.getStateAction());
            switch (action) {
                case PUBLISH_EVENT:
                    checkIfEventAlreadyPublished(toBeUpdated);
                    checkIfEventIsCanceled(toBeUpdated);
                    toBeUpdated.setEventStatus(EventStatus.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    checkIfEventAlreadyPublished(toBeUpdated);
                    toBeUpdated.setEventStatus(EventStatus.CANCELED);
                    break;
            }
        }
    }

    private void checkIfEventIsCanceled(Event toBeUpdated) {
        if (toBeUpdated.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new IllegalPublicationException("Event is canceled and may not be published");
        }
    }

    private void checkIfEventAlreadyPublished(Event toBeUpdated) {
        if (toBeUpdated.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalPublicationException("Event is already published");
        }
    }

    private void updateStatusUser(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getStateAction() != null) {
            StateUserAction action = parseActionUser(updateRequest.getStateAction());
            switch (action) {
                case CANCEL_REVIEW:
                    checkIfEventAlreadyPublished(toBeUpdated);
                    toBeUpdated.setEventStatus(EventStatus.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    toBeUpdated.setEventStatus(EventStatus.PENDING);
                    break;
            }
        }
    }

    private StateAdminAction parseActionAdmin(String stateAction) {
        try {
            return StateAdminAction.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown action");
        }
    }

    private StateUserAction parseActionUser(String stateAction) {
        try {
            return StateUserAction.valueOf(stateAction);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown action");
        }
    }

    private void updateLocation(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getLocation() != null) {
            if (!(updateRequest.getLocation().getLat().equals(toBeUpdated.getLocation().getLat()) &&
                  updateRequest.getLocation().getLon().equals(toBeUpdated.getLocation().getLon()))) {
                toBeUpdated.setLocation(saveLocation(updateRequest.getLocation()));
            }
        }
    }

    private void updateEventDate(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getEventDate() != null) {
            LocalDateTime newDateTime = LocalDateTime.parse(updateRequest.getEventDate(), REQUEST_TIME_FORMAT);
            validateEventDate(newDateTime);
            toBeUpdated.setEventDate(newDateTime);
        }
    }

    private void updateCategory(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getCategory() != null) {
            if (!(updateRequest.getCategory().longValue() == toBeUpdated.getCategory().getId())) {
                toBeUpdated.setCategory(getCategory(updateRequest.getCategory()));
            }
        }
    }

    private void updatePaid(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getPaid() != null) {
            toBeUpdated.setPaid(updateRequest.getPaid());
        }
    }

    private void updateTitle(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getTitle() != null) {
            toBeUpdated.setTitle(updateRequest.getTitle());
        }
    }

    private void updateParticipantLimit(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getParticipantLimit() != null) {
            toBeUpdated.setParticipationLimit(updateRequest.getParticipantLimit());
        }
    }

    private void updateAnnotation(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            toBeUpdated.setAnnotation(updateRequest.getAnnotation());
        }
    }

    private void updateDescription(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getDescription() != null) {
            toBeUpdated.setDescription(updateRequest.getDescription());
        }
    }

    private EventStatus parseEventStatus(String eventStatus) {
        try {
            return EventStatus.valueOf(eventStatus);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown event status");
        }
    }

    private SortType parseSortType(String sort) {
        try {
            return SortType.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new UnknownActionException("Unknown sorting type");
        }
    }

    private Pageable makePageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, REQUEST_TIME_FORMAT);
    }
}