package ewm_server.mapper;

import ewm_dto.domain_dto.StatsRequestDto;
import ewm_dto.domain_dto.StatsResponseDto;
import ewm_server.model.StatsRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private final static DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    public static StatsRecord mapRequestToModel(StatsRequestDto dto) {
        StatsRecord record = new StatsRecord();
        record.setApp(dto.getApp());
        record.setUri(dto.getUri());
        record.setTimestamp(LocalDateTime.parse(dto.getTimestamp(), REQUEST_TIME_FORMAT));
        return record;
    }

    public static StatsResponseDto mapModelToResponse(StatsRecord model) {
        return StatsResponseDto.builder()
                .app(model.getApp())
                .uri(model.getUri())
                .hits(model.getHits())
                .build();
    }
}