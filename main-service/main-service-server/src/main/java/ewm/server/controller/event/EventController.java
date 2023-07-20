package ewm.server.controller.event;

import ewm.server.dto.EventFullDto;
import ewm.server.dto.NewEventDto;
import ewm.server.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class EventController {
    private static final String EVENT_PRIVATE_PATH = "/users/{userId}/events";
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(value = EVENT_PRIVATE_PATH)
    public ResponseEntity<EventFullDto> addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(userId, newEventDto));
    }
}