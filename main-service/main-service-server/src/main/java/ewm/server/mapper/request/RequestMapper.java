package ewm.server.mapper.request;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.model.request.ParticipationRequest;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto mapModelToDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getRequestId())
                .created(request.getCreated().format(DATE_TIME_FORMAT))
                .requester(request.getRequester().getUserId())
                .event(request.getEvent().getEventId())
                .status(request.getRequestStatus().toString())
                .build();
    }
}