package ewm.server.controller.event;

import ewm.server.dto.event.EventFullDto;
import ewm.server.dto.event.NewEventDto;
import ewm.server.dto.event.UpdateEventRequest;
import ewm.server.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class EventController {
    private static final String EVENT_PRIVATE_PATH = "/users/{userId}/events";
    private static final String EVENT_ADMIN_PATH = "/admin/events";
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(value = EVENT_PRIVATE_PATH)
    public ResponseEntity<EventFullDto> addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(userId, newEventDto));
    }

    @PatchMapping(value = EVENT_ADMIN_PATH + "/{eventId}")
    public ResponseEntity<EventFullDto> updateEventAdmin(@PathVariable("eventId") Long eventId,
                                                         @Valid @RequestBody UpdateEventRequest updateRequest) {
        return ResponseEntity.ok().body(eventService.updateEventAdmin(eventId, updateRequest));
    }

    @PatchMapping(value = EVENT_PRIVATE_PATH + "/{eventId}")
    public ResponseEntity<EventFullDto> updateEventUser(@PathVariable("userId") Long userId,
                                                        @PathVariable("eventId") Long eventId,
                                                        @Valid @RequestBody UpdateEventRequest updateRequest) {
        return ResponseEntity.ok().body(eventService.updateEventUser(userId, eventId, updateRequest));
    }
}