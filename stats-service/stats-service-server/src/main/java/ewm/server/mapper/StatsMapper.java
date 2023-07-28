package ewm.server.mapper;

import ewm.dto.StatsRequestDto;
import ewm.server.model.StatsRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatsRecord statsRequestDtoToStatsRecord(StatsRequestDto statsRequestDto) {

        return StatsRecord
                .builder()
                .app(statsRequestDto.getApp())
                .uri(statsRequestDto.getUri())
                .ip(statsRequestDto.getIp())
                .timestamp(LocalDateTime.parse(statsRequestDto.getTimestamp(), DATE_TIME_FORMATTER))
                .build();
    }
}