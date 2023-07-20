package ewm.server.service.event;

import ewm.server.dto.event.EventFullDto;
import ewm.server.dto.event.NewEventDto;
import ewm.server.dto.event.UpdateEventRequest;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);
    EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest updateRequest);

    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventRequest updateRequest);
}