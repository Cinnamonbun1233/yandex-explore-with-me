package ewm.server.mapper.request;

import ewm.server.dto.request.ParticipationRequestDto;
import ewm.server.model.request.ParticipationRequest;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto participationRequestToParticipationRequestDto(ParticipationRequest participationRequest) {

        return ParticipationRequestDto
                .builder()
                .id(participationRequest.getRequestId())
                .created(participationRequest.getCreated().format(DATE_TIME_FORMAT))
                .requester(participationRequest.getRequester().getUserId())
                .event(participationRequest.getEvent().getEventId())
                .status(participationRequest.getRequestStatus())
                .build();
    }
}