package ewm.server.service.event;

import ewm.server.dto.event.*;
import ewm.server.dto.request.ParticipationRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventFullDto createNewEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllUsersEvents(Long userId, Pageable pageable);

    List<ParticipationRequestDto> getRequestsToUsersEvent(Long userId, Long eventId);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId);

    EventFullDto getEventByIdPublic(Long eventId);

    List<EventFullDto> searchEventsAdmin(Optional<Integer[]> users,
                                         Optional<String[]> states,
                                         Optional<Integer[]> categories,
                                         Optional<String> rangeStart,
                                         Optional<String> rangeEnd,
                                         Pageable pageable);

    List<EventShortDto> searchEventsPublic(Optional<String> text,
                                           Optional<Integer[]> categories,
                                           Optional<Boolean> paid,
                                           Optional<String> rangeStart,
                                           Optional<String> rangeEnd,
                                           Boolean onlyAvailable,
                                           String sort,
                                           Pageable pageable);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateEventRequest);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventRequest updateEventRequest);

    EventRequestStatusUpdateResult updateRequestByInitiator(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}