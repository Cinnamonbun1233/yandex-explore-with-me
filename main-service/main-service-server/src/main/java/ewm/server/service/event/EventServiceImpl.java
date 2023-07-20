package ewm.server.service.event;

import ewm.server.dto.event.*;
import ewm.server.exception.CategoryNotFoundException;
import ewm.server.exception.EventNotFoundException;
import ewm.server.exception.UnknownActionException;
import ewm.server.exception.UserNotFoundException;
import ewm.server.mapper.EventMapper;
import ewm.server.mapper.LocationMapper;
import ewm.server.model.category.Category;
import ewm.server.model.event.Event;
import ewm.server.model.event.EventStatus;
import ewm.server.model.event.Location;
import ewm.server.model.user.User;
import ewm.server.repo.category.CategoryRepo;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.event.LocationRepo;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;
    private final LocationRepo locationRepo;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event newEvent = EventMapper.mapDtoToModel(newEventDto);
        newEvent.setCategory(getCategory(newEventDto.getCategory()));
        newEvent.setLocation(saveLocation(newEventDto.getLocation()));
        newEvent.setInitiator(getInitiator(userId));
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setEventStatus(EventStatus.PENDING);
        return EventMapper.mapModelToDto(eventRepo.save(newEvent));
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest) {
        Event toBeUpdated = getEvent(eventId);
        updateEvent(toBeUpdated, updateRequest);
        updateStatusAdmin(toBeUpdated, updateRequest);
        return EventMapper.mapModelToDto(eventRepo.save(toBeUpdated));
    }

    @Override
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventRequest updateRequest) {
        checkIfUserExists(userId);
        Event toBeUpdated = getEvent(eventId);
        updateEvent(toBeUpdated, updateRequest);
        updateStatusUser(toBeUpdated, updateRequest);
        return EventMapper.mapModelToDto(eventRepo.save(toBeUpdated));
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
                    toBeUpdated.setEventStatus(EventStatus.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    toBeUpdated.setEventStatus(EventStatus.CANCELLED);
                    break;
            }
        }
    }

    private void updateStatusUser(Event toBeUpdated, UpdateEventRequest updateRequest) {
        if (updateRequest.getStateAction() != null) {
            StateUserAction action = parseActionUser(updateRequest.getStateAction());
            switch (action) {
                case CANCEL_REVIEW:
                    toBeUpdated.setEventStatus(EventStatus.CANCELLED);
                case SEND_TO_REVIEW:
                    toBeUpdated.setEventStatus(EventStatus.PENDING);
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
            toBeUpdated.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), REQUEST_TIME_FORMAT));
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
}