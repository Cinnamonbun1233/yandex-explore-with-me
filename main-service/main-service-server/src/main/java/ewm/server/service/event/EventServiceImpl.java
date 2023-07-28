package ewm.server.service.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import ewm.client.StatsClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Value("${date-time.format}")
    private String dateTimePattern;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;
    private final LocationRepo locationRepo;
    private final RequestRepo requestRepo;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public EventFullDto createNewEvent(Long userId, NewEventDto newEventDto) {

        Event newEvent = EventMapper.mapDtoToModel(newEventDto);

        validateEventDate(newEvent.getEventDate());

        newEvent.setCategory(getCategory(newEventDto.getCategory()));
        newEvent.setLocation(saveLocation(newEventDto.getLocation()));
        newEvent.setInitiator(getInitiator(userId));
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setEventStatus(EventStatus.PENDING);

        Event savedEvent = eventRepo.save(newEvent);

        return EventMapper.mapModelToFullDto(savedEvent, statsClient);
    }

    @Transactional
    @Override
    public List<EventShortDto> getAllUsersEvents(Long userId, Pageable pageable) {

        BooleanExpression byUserId = QEvent.event.initiator.userId.eq(userId);

        return eventRepo
                .findAll(byUserId, pageable)
                .stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .map(event -> EventMapper.mapModelToShortDto(event, statsClient))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<ParticipationRequestDto> getRequestsToUsersEvent(Long userId, Long eventId) {

        checkIfUserExists(userId);

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));

        return event
                .getRequests()
                .stream()
                .map(RequestMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {

        checkIfUserExists(userId);

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));

        return EventMapper.mapModelToFullDto(event, statsClient);
    }

    @Transactional
    @Override
    public EventFullDto getEventByIdPublic(Long eventId) {

        Event event = eventRepo.findByEventIdAndEventStatus(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));

        return EventMapper.mapModelToFullDto(event, statsClient);
    }

    @Transactional
    @Override
    public List<EventFullDto> searchEventsAdmin(Optional<Integer[]> users,
                                                Optional<String[]> states,
                                                Optional<Integer[]> categories,
                                                Optional<String> rangeStart,
                                                Optional<String> rangeEnd,
                                                Pageable pageable) {

        BooleanExpression searchExp = makeSearchExpAdmin(users, states, categories, rangeStart, rangeEnd);

        return eventRepo
                .findAll(searchExp, pageable)
                .stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .map(event -> EventMapper.mapModelToFullDto(event, statsClient))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<EventShortDto> searchEventsPublic(Optional<String> text,
                                                  Optional<Integer[]> categories,
                                                  Optional<Boolean> paid,
                                                  Optional<String> rangeStart,
                                                  Optional<String> rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort,
                                                  Pageable pageable) {

        BooleanExpression searchExp = makeSearchExpPublic(text, categories, paid, rangeStart, rangeEnd);

        Comparator<EventShortDto> comparator = makeComparator(sort);

        if (onlyAvailable) {
            return eventRepo
                    .findAll(searchExp, pageable)
                    .stream()
                    .filter(event -> event
                                             .getRequests()
                                             .stream()
                                             .filter(participationRequest -> participationRequest
                                                     .getRequestStatus()
                                                     .equals(RequestStatus.CONFIRMED))
                                             .count() < event.getParticipationLimit())
                    .map(event -> EventMapper.mapModelToShortDto(event, statsClient))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            return eventRepo
                    .findAll(searchExp, pageable)
                    .stream()
                    .map(event -> EventMapper.mapModelToShortDto(event, statsClient))
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateEventRequest) {

        Event toBeUpdated = getEvent(eventId);
        updateEvent(toBeUpdated, updateEventRequest);
        updateStatusAdmin(toBeUpdated, updateEventRequest);
        Event savedEvent = eventRepo.save(toBeUpdated);

        return EventMapper.mapModelToFullDto(savedEvent, statsClient);
    }

    @Transactional
    @Override
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {

        checkIfUserExists(userId);
        Event toBeUpdated = getEvent(eventId);
        checkIfEventAlreadyPublished(toBeUpdated);
        updateEvent(toBeUpdated, updateEventRequest);
        updateStatusUser(toBeUpdated, updateEventRequest);
        Event savedEvent = eventRepo.save(toBeUpdated);

        return EventMapper.mapModelToFullDto(savedEvent, statsClient);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestByInitiator(Long userId,
                                                                   Long eventId,
                                                                   EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        checkIfUserExists(userId);
        RequestStatus requestStatus = parseRequestStatus(eventRequestStatusUpdateRequest.getStatus());
        List<ParticipationRequest> toBeUpdated = makeListOfRequestsToBeUpdated(eventId, eventRequestStatusUpdateRequest);
        checkIfUpdateRequestIsValid(requestStatus, toBeUpdated);
        toBeUpdated.forEach(participationRequest -> participationRequest.setRequestStatus(requestStatus));
        List<ParticipationRequest> updatedRequests = requestRepo.saveAllAndFlush(toBeUpdated);
        rejectPendingRequestsIfParticipantLimitIsReached(updatedRequests);

        return EventRequestStatusUpdateResult
                .builder()
                .confirmedRequests(requestRepo.findAllByRequestStatusAndEventEventId(RequestStatus.CONFIRMED, eventId)
                        .stream()
                        .map(RequestMapper::mapModelToDto)
                        .collect(Collectors.toList()))
                .rejectedRequests(requestRepo.findAllByRequestStatusAndEventEventId(RequestStatus.REJECTED, eventId)
                        .stream()
                        .map(RequestMapper::mapModelToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private void rejectPendingRequestsIfParticipantLimitIsReached(List<ParticipationRequest> updatedRequests) {

        updatedRequests
                .stream()
                .filter(participationRequest -> participationRequest
                                                        .getEvent()
                                                        .getParticipationLimit() != 0 && participationRequest
                                                                                                 .getEvent()
                                                                                                 .getParticipationLimit() == participationRequest
                                                                                                 .getEvent()
                                                                                                 .getRequests()
                                                                                                 .stream()
                                                                                                 .filter(request -> request
                                                                                                         .getRequestStatus()
                                                                                                         .equals(RequestStatus.CONFIRMED))
                                                                                                 .count())
                .filter(participationRequest -> participationRequest
                        .getRequestStatus()
                        .equals(RequestStatus.PENDING))
                .forEach(participationRequest -> participationRequest
                        .setRequestStatus(RequestStatus.REJECTED));

        requestRepo.saveAllAndFlush(updatedRequests);
    }

    private void checkIfUpdateRequestIsValid(RequestStatus requestStatus, List<ParticipationRequest> toBeUpdated) {

        if (toBeUpdated
                .stream()
                .anyMatch(participationRequest -> !participationRequest
                        .getRequestStatus()
                        .equals(RequestStatus.PENDING))) {
            throw new IllegalRequestException("Status update is available for pending requests only");
        }

        if (requestStatus
                    .equals(RequestStatus.CONFIRMED) && toBeUpdated
                    .stream()
                    .anyMatch(participationRequest -> participationRequest
                                                              .getEvent()
                                                              .getParticipationLimit() != 0 &&
                                                      participationRequest
                                                              .getEvent()
                                                              .getParticipationLimit() == participationRequest
                                                              .getEvent()
                                                              .getRequests()
                                                              .stream()
                                                              .filter(request -> request
                                                                      .getRequestStatus()
                                                                      .equals(RequestStatus.CONFIRMED))
                                                              .count())
        ) {
            throw new IllegalRequestException("Participant limit to one of events has been already reached");
        }
    }

    private void validateEventDate(LocalDateTime localDateTime) {

        if (LocalDateTime
                    .now()
                    .minusHours(2)
                    .isAfter(localDateTime) || localDateTime
                    .isBefore(LocalDateTime.now())) {
            throw new IllegalDatesException("Event date has to be at least 2 hours after current moment");
        }
    }

    private List<ParticipationRequest> makeListOfRequestsToBeUpdated(Long eventId,
                                                                     EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        QParticipationRequest qParticipationRequest = QParticipationRequest.participationRequest;
        BooleanExpression booleanExpression = qParticipationRequest
                .event
                .eventId
                .eq(eventId)
                .and(qParticipationRequest.requestId.in(eventRequestStatusUpdateRequest.getRequestIds()));

        return StreamSupport
                .stream(requestRepo.findAll(booleanExpression).spliterator(), false)
                .collect(Collectors.toList());
    }

    private RequestStatus parseRequestStatus(String status) {

        try {
            return RequestStatus.valueOf(status);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new UnknownActionException("Status is unknown");
        }
    }

    private Comparator<EventShortDto> makeComparator(String sort) {

        SortType sortType = parseSortType(sort);

        if (sortType.equals(SortType.VIEWS)) {
            return Comparator.comparing(EventShortDto::getViews);
        }

        return Comparator.comparing(EventShortDto::getEventDate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BooleanExpression makeSearchExpAdmin(Optional<Integer[]> users,
                                                 Optional<String[]> states,
                                                 Optional<Integer[]> categories,
                                                 Optional<String> rangeStart,
                                                 Optional<String> rangeEnd) {

        QEvent qEvent = QEvent.event;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        users.ifPresent(userIds -> booleanBuilder.and(qEvent.initiator.userId.in(userIds)));
        states.ifPresent(stateStrings -> booleanBuilder.and(qEvent.eventStatus.in(Arrays.stream(states.get())
                .map(this::parseEventStatus)
                .toArray(EventStatus[]::new))));
        categories.ifPresent(categoryIds -> booleanBuilder.and(qEvent.category.categoryId.in(categoryIds)));
        rangeStart.ifPresent(start -> booleanBuilder.and(qEvent.eventDate.after(parseDateTime(start))));
        rangeEnd.ifPresent(end -> booleanBuilder.and(qEvent.eventDate.before(parseDateTime(end))));

        return booleanBuilder.getValue() != null ? Expressions.asBoolean(booleanBuilder.getValue()) : qEvent.isNotNull();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private BooleanExpression makeSearchExpPublic(Optional<String> text,
                                                  Optional<Integer[]> categories,
                                                  Optional<Boolean> paid,
                                                  Optional<String> rangeStart,
                                                  Optional<String> rangeEnd) {

        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.eventStatus.eq(EventStatus.PUBLISHED));

        text.ifPresent(str -> {
            BooleanExpression annotationContainsText = qEvent.annotation.likeIgnoreCase(str);
            BooleanExpression descriptionContainsText = qEvent.description.likeIgnoreCase(str);
            builder.and(annotationContainsText.or(descriptionContainsText));
        });

        categories.ifPresent(categoryIds -> builder.and(qEvent.category.categoryId.in(categoryIds)));

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

    private void validateSearchDates(String startPeriod, String endPeriod) {

        if (parseDateTime(startPeriod).isAfter(parseDateTime(endPeriod))) {
            throw new IllegalDatesException("Range start has to be after range end");
        }
    }

    private Location saveLocation(LocationDto locationDto) {

        return locationRepo.save(LocationMapper.mapDtoToModel(locationDto));
    }

    private Category getCategory(Integer categoryId) {

        return categoryRepo.findById(categoryId.longValue())
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category %d does not exist", categoryId)));
    }

    private User getInitiator(Long userId) {

        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User %d does not exist", userId)));
    }

    private Event getEvent(Long eventId) {

        return eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d does not exist", eventId)));
    }

    private void checkIfUserExists(Long userId) {

        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(String.format("User %d does not exist", userId));
        }
    }

    private void updateEvent(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        updateDescription(toBeUpdated, updateEventRequest);
        updateAnnotation(toBeUpdated, updateEventRequest);
        updateParticipantLimit(toBeUpdated, updateEventRequest);
        updateTitle(toBeUpdated, updateEventRequest);
        updatePaid(toBeUpdated, updateEventRequest);
        updateCategory(toBeUpdated, updateEventRequest);
        updateEventDate(toBeUpdated, updateEventRequest);
        updateLocation(toBeUpdated, updateEventRequest);
    }

    private void updateStatusAdmin(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getStateAction() != null) {

            StateAdminAction stateAdminAction = parseActionAdmin(updateEventRequest.getStateAction());

            switch (stateAdminAction) {

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

    private void updateStatusUser(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getStateAction() != null) {

            StateUserAction stateUserAction = parseActionUser(updateEventRequest.getStateAction());

            switch (stateUserAction) {

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

    private void updateLocation(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getLocation() != null) {

            if (!(updateEventRequest.getLocation().getLat().equals(toBeUpdated.getLocation().getLat()) &&
                  updateEventRequest.getLocation().getLon().equals(toBeUpdated.getLocation().getLon()))) {
                toBeUpdated.setLocation(saveLocation(updateEventRequest.getLocation()));
            }
        }
    }

    private void updateEventDate(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getEventDate() != null) {

            LocalDateTime localDateTime = LocalDateTime.parse(updateEventRequest.getEventDate(),
                    DateTimeFormatter.ofPattern(dateTimePattern));

            validateEventDate(localDateTime);
            toBeUpdated.setEventDate(localDateTime);
        }
    }

    private void updateCategory(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getCategory() != null) {
            if (!(updateEventRequest.getCategory().longValue() == toBeUpdated.getCategory().getCategoryId())) {
                toBeUpdated.setCategory(getCategory(updateEventRequest.getCategory()));
            }
        }
    }

    private void updatePaid(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getPaid() != null) {
            toBeUpdated.setPaid(updateEventRequest.getPaid());
        }
    }

    private void updateTitle(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getTitle() != null) {
            toBeUpdated.setTitle(updateEventRequest.getTitle());
        }
    }

    private void updateParticipantLimit(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getParticipantLimit() != null) {
            toBeUpdated.setParticipationLimit(updateEventRequest.getParticipantLimit());
        }
    }

    private void updateAnnotation(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getAnnotation() != null) {
            toBeUpdated.setAnnotation(updateEventRequest.getAnnotation());
        }
    }

    private void updateDescription(Event toBeUpdated, UpdateEventRequest updateEventRequest) {

        if (updateEventRequest.getDescription() != null) {
            toBeUpdated.setDescription(updateEventRequest.getDescription());
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

    private LocalDateTime parseDateTime(String dateTimeString) {

        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(dateTimePattern));
    }
}