package ewm.server.controller.request;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private static final String CANCEL_OWN_REQUEST_PATH = "/{requestId}/cancel";
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createNewRequest(@PathVariable("userId") Long userId,
                                                                    @RequestParam("eventId") Long eventId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.createNewRequest(userId, eventId));
    }

    @PatchMapping(value = CANCEL_OWN_REQUEST_PATH)
    public ResponseEntity<ParticipationRequestDto> cancelOwnRequestById(@PathVariable("userId") Long userId,
                                                                        @PathVariable("requestId") Long requestId) {

        return ResponseEntity
                .ok()
                .body(requestService.cancelOwnRequestById(userId, requestId));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUsersRequestsById(@PathVariable("userId") Long userId) {

        return ResponseEntity
                .ok()
                .body(requestService.getUsersRequestsById(userId));
    }
}