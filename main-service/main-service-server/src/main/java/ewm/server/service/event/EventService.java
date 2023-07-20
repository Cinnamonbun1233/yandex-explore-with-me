package ewm.server.service.event;

import ewm.server.dto.EventFullDto;
import ewm.server.dto.NewEventDto;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);
}