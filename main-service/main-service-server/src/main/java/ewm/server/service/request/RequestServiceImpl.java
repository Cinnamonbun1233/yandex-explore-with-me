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
import java.util.Objects;
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
    public ParticipationRequestDto createNewRequest(Long userId, Long eventId) {

        checkIfRequestWasAlreadyCreated(userId, eventId);
        checkIfInitiatorIsCreatingRequest(userId, eventId);
        ParticipationRequest participationRequest = new ParticipationRequest();

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d does not exist", eventId)));

        checkIfEventIsPublished(event);
        checkIfParticipantLimitIsFull(event);

        participationRequest.setRequester(userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User %d does not exist", userId))));

        participationRequest.setEvent(event);

        if (event.getParticipationLimit() == 0 || !event.getRequestModeration()) {
            participationRequest.setRequestStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setRequestStatus(RequestStatus.PENDING);
        }

        participationRequest.setCreated(LocalDateTime.now());

        return RequestMapper.participationRequestToParticipationRequestDto(requestRepo.save(participationRequest));
    }

    @Transactional
    @Override
    public List<ParticipationRequestDto> getUsersRequestsById(Long userId) {

        QParticipationRequest qParticipationRequest = QParticipationRequest.participationRequest;
        BooleanExpression booleanExpression = qParticipationRequest.requester.userId.eq(userId);

        return StreamSupport
                .stream(requestRepo.findAll(booleanExpression).spliterator(), false)
                .map(RequestMapper::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelOwnRequestById(Long userId, Long requestId) {

        checkIfUserExists(userId);

        ParticipationRequest participationRequest = requestRepo.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Request %d does not exist", requestId)));

        participationRequest.setRequestStatus(RequestStatus.CANCELED);

        return RequestMapper.participationRequestToParticipationRequestDto(requestRepo.save(participationRequest));
    }

    private void checkIfUserExists(Long userId) {

        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User %d does not exist", userId)));
    }

    private void checkIfParticipantLimitIsFull(Event event) {

        if (event.getParticipationLimit() != 0) {
            if (event.getParticipationLimit() == event
                    .getRequests()
                    .stream()
                    .filter(participationRequest -> participationRequest.getRequestStatus().equals(RequestStatus.CONFIRMED))
                    .count()) {
                throw new IllegalRequestException("A participant limit has been reached");
            }
        }
    }

    private void checkIfEventIsPublished(Event event) {

        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new IllegalRequestException("Event has not been published yet");
        }
    }

    private void checkIfInitiatorIsCreatingRequest(Long userId, Long eventId) {

        if (Objects.equals(eventRepo.findById(eventId).orElseThrow().getInitiator().getUserId(), userId)) {
            throw new IllegalRequestException("Initiator may not create request to participate in his own event");
        }
    }

    private void checkIfRequestWasAlreadyCreated(Long userId, Long eventId) {

        if (requestRepo.findByRequesterUserIdAndEventEventId(userId, eventId).isPresent()) {
            throw new IllegalRequestException("Request was already created");
        }
    }
}