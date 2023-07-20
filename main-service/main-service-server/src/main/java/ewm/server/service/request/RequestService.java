package ewm.server.service.request;

import ewm.server.dto.request.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);
}