package ewm.server.service.request;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.exception.event.EventNotFoundException;
import ewm.server.exception.request.RequestNotFoundException;
import ewm.server.exception.user.UserNotFoundException;
import ewm.server.mapper.request.RequestMapper;
import ewm.server.model.event.QParticipationRequest;
import ewm.server.model.request.ParticipationRequest;
import ewm.server.model.request.RequestStatus;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.request.RequestRepo;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Override
    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        checkIfUserExists(userId);
        ParticipationRequest toBeCanceled = requestRepo.findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException("Request does not exist");
        });
        toBeCanceled.setRequestStatus(RequestStatus.CANCELED);
        return RequestMapper.mapModelToDto(requestRepo.save(toBeCanceled));
    }

    private void checkIfUserExists(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        });
    }

    @Override
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        QParticipationRequest qRequest = QParticipationRequest.participationRequest;
        BooleanExpression byRequesterId = qRequest.requester.id.eq(userId);
        return StreamSupport.stream(requestRepo.findAll(byRequesterId).spliterator(), false)
                .map(RequestMapper::mapModelToDto)
                .collect(Collectors.toList());
    }
}