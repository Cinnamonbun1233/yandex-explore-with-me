package ewm.server.mapper;

import ewm.dto.StatsRequestDto;
import ewm.server.model.StatsRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatsRecord mapRequestToModel(StatsRequestDto statsRequestDto) {
        StatsRecord record = new StatsRecord();
        record.setApp(statsRequestDto.getApp());
        record.setUri(statsRequestDto.getUri());
        record.setIp(statsRequestDto.getIp());
        record.setTimestamp(LocalDateTime.parse(statsRequestDto.getTimestamp(), DATE_TIME_FORMATTER));
        return record;
    }
}