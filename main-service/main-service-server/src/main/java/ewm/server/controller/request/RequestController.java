package ewm.server.controller.request;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;
    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }
    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable("userId") Long userId,
                                                              @RequestParam("eventId") Long eventId) {
        return ResponseEntity.ok().body(requestService.addRequest(userId, eventId));
    }
}