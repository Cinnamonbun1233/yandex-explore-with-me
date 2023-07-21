package ewm.server.mapper.request;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.model.event.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto mapModelToDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getRequestStatus().toString())
                .build();
    }
}