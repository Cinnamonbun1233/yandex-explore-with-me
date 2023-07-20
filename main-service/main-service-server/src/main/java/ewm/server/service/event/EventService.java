package ewm.server.service.event;

import ewm.server.dto.event.EventFullDto;
import ewm.server.dto.event.NewEventDto;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);
}