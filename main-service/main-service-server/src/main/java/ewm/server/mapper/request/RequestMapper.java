package ewm.server.mapper.request;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.model.request.ParticipationRequest;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static ParticipationRequestDto mapModelToDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(REQUEST_TIME_FORMAT))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .status(request.getRequestStatus().toString())
                .build();
    }
}