package ewm.server.service.event;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.exception.EventNotFoundException;
import ewm.server.exception.UserNotFoundException;
import ewm.server.mapper.RequestMapper;
import ewm.server.model.event.ParticipationRequest;
import ewm.server.model.event.RequestStatus;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.event.RequestRepo;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepo requestRepo;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setRequester(userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        }));
        newRequest.setEvent(eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event does not exist");
        }));
        newRequest.setRequestStatus(RequestStatus.PENDING);
        newRequest.setCreated(LocalDateTime.now());
        return RequestMapper.mapModelToDto(requestRepo.save(newRequest));
    }
}