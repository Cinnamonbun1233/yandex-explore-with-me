package ewm.server.service.event;

import ewm.server.dto.event.EventFullDto;
import ewm.server.dto.event.EventShortDto;
import ewm.server.dto.event.NewEventDto;
import ewm.server.dto.event.UpdateEventRequest;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventRequest updateRequest);

    List<EventFullDto> searchEventsAdmin(Optional<Integer[]> users, Optional<String[]> states,
                                         Optional<Integer[]> categories, Optional<String> rangeStart,
                                         Optional<String> rangeEnd, int from, int size);

    List<EventShortDto> getAllUsersEvents(Long userId, int from, int size);

    List<EventShortDto> searchEventsPublic(Optional<String> text, Optional<Integer[]> categories, Optional<Boolean> paid,
                                           Optional<String> rangeStart, Optional<String> rangeEnd,
                                           Boolean onlyAvailable, String sort, int from, int size);

    EventFullDto getEventByIdPublic(Long id);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId);
}