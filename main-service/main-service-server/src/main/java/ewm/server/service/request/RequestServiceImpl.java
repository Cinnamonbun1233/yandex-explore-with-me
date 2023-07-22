package ewm.server.service.request;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.exception.event.EventNotFoundException;
import ewm.server.exception.request.IllegalRequestException;
import ewm.server.exception.request.RequestNotFoundException;
import ewm.server.exception.user.UserNotFoundException;
import ewm.server.mapper.request.RequestMapper;
import ewm.server.model.event.Event;
import ewm.server.model.event.EventStatus;
import ewm.server.model.request.ParticipationRequest;
import ewm.server.model.request.QParticipationRequest;
import ewm.server.model.request.RequestStatus;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.request.RequestRepo;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        checkIfRequestWasAlreadyCreated(userId, eventId);
        checkIfInitiatorIsCreatingRequest(userId, eventId);
        ParticipationRequest newRequest = new ParticipationRequest();
        Event eventFound = eventRepo.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException("Event does not exist");
        });
        checkIfEventIsPublished(eventFound);
        checkIfParticipantLimitIsFull(eventFound);
        newRequest.setRequester(userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        }));
        newRequest.setEvent(eventFound);
        if(eventFound.getParticipationLimit() == 0 || eventFound.getRequestModeration() == false) {
            newRequest.setRequestStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setRequestStatus(RequestStatus.PENDING);
        }
        newRequest.setCreated(LocalDateTime.now());
        return RequestMapper.mapModelToDto(requestRepo.save(newRequest));
    }

    private void checkIfParticipantLimitIsFull(Event eventFound) {
        if(eventFound.getParticipationLimit() != 0) {
            if (eventFound.getParticipationLimit() == eventFound.getRequests().stream()
                    .filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED)).count()) {
                throw new IllegalRequestException("Participant limit has been reached");
            }
        }
    }

    private void checkIfEventIsPublished(Event event) {
        if(!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalRequestException("Event has not been published yet");
        }
    }

    private void checkIfInitiatorIsCreatingRequest(Long userId, Long eventId) {
        if(eventRepo.findById(eventId).orElseThrow().getInitiator().getId() == userId) {
            throw new IllegalRequestException("Initiator may not create request to participate in his own event");
        }
    }

    private void checkIfRequestWasAlreadyCreated(Long userId, Long eventId) {
        if(requestRepo.findByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new IllegalRequestException("Request was already created");
        }
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        checkIfUserExists(userId);
        ParticipationRequest toBeCanceled = requestRepo.findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException("Request does not exist");
        });
        toBeCanceled.setRequestStatus(RequestStatus.CANCELED);
        return RequestMapper.mapModelToDto(requestRepo.save(toBeCanceled));
    }

    @Override
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        QParticipationRequest qRequest = QParticipationRequest.participationRequest;
        BooleanExpression byRequesterId = qRequest.requester.id.eq(userId);
        return StreamSupport.stream(requestRepo.findAll(byRequesterId).spliterator(), false)
                .map(RequestMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        });
    }
}