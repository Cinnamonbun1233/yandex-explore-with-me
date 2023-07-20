package ewm.server.service.event;

import ewm.server.dto.request.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);
}