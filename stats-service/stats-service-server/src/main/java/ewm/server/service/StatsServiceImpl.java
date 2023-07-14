package ewm.server.service;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import ewm.server.mapper.StatsMapper;
import ewm.server.repo.StatsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private final static DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepo statsRepo;

    @Autowired
    public StatsServiceImpl(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    @Override
    @Transactional
    public void saveRecord(StatsRequestDto request) {
        log.info("RECORD SAVED");
        statsRepo.save(StatsMapper.mapRequestToModel(request));
    }

    @Override
    public List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique) {
        if (unique == null && uris == null) {
            return statsRepo.getStatsForDates(parseDateTime(start), parseDateTime(end));
        } else if (unique != null && uris == null) {
            if (Boolean.parseBoolean(unique)) {
                return statsRepo.getStatsForDatesWithUniqueIp(parseDateTime(start), parseDateTime(end));
            } else {
                return statsRepo.getStatsForDates(parseDateTime(start), parseDateTime(end));
            }
        } else if (unique == null) {
            return statsRepo.getStatsForDatesAndUris(parseDateTime(start), parseDateTime(end), uris);
        } else {
            if (Boolean.parseBoolean(unique)) {
                return statsRepo.getStatsForDatesAndUrisWithUniqueIp(parseDateTime(start), parseDateTime(end), uris);
            } else {
                return statsRepo.getStatsForDatesAndUris(parseDateTime(start), parseDateTime(end), uris);
            }
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, REQUEST_TIME_FORMAT);
    }
}