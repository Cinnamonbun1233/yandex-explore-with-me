package ewm.server.service.request;

import ewm.server.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUsersRequests(Long userId);
}