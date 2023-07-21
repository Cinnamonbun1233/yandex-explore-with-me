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
import java.util.List;
import java.util.Optional;

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

    @GetMapping(EVENT_ADMIN_PATH)
    public ResponseEntity<List<EventFullDto>> searchEventsAdmin(@RequestParam(name = "users", required = false) Optional<Integer[]> users,
                                                                @RequestParam(name = "states", required = false) Optional<String[]> states,
                                                                @RequestParam(name = "categories", required = false) Optional<Integer[]> categories,
                                                                @RequestParam(name = "rangeStart", required = false) Optional<String> rangeStart,
                                                                @RequestParam(name = "rangeEnd", required = false) Optional<String> rangeEnd,
                                                                @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok().body(eventService.searchEventsAdmin(users, states, categories, rangeStart, rangeEnd,
                from, size));
    }
}