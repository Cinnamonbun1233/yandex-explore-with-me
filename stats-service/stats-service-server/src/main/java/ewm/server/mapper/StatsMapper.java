package ewm.server.mapper;

import ewm.dto.StatsRequestDto;
import ewm.server.model.StatsRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatsRecord mapRequestToModel(StatsRequestDto statsRequestDto) {
        StatsRecord record = new StatsRecord();
        record.setApp(statsRequestDto.getApp());
        record.setUri(statsRequestDto.getUri());
        record.setIp(statsRequestDto.getIp());
        record.setTimestamp(LocalDateTime.parse(statsRequestDto.getTimestamp(), REQUEST_TIME_FORMAT));
        return record;
    }
}