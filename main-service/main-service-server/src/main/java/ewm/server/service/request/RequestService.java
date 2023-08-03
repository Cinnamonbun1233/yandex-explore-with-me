package ewm.server.service.request;

import ewm.server.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createNewRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getUsersRequestsById(Long userId);

    ParticipationRequestDto cancelOwnRequestById(Long userId, Long requestId);
}