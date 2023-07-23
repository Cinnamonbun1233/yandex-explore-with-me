package ewm.server.service;

import ewm.server.exception.IllegalDatesException;
import ewm.server.mapper.StatsMapper;
import ewm.server.repo.StatsRepo;
import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepo statsRepo;

    @Autowired
    public StatsServiceImpl(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    @Override
    @Transactional
    public void saveRecord(StatsRequestDto request, HttpServletRequest meta) {
        log.info(meta.getRemoteAddr());
        request.setIp(meta.getRemoteAddr());
        statsRepo.save(StatsMapper.mapRequestToModel(request));
        log.info("RECORD SAVED");
    }

    @Override
    public List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique) {
        validateDates(start, end);
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

    private void validateDates(String start, String end) {
        if (parseDateTime(start).isAfter(parseDateTime(end))) {
            throw new IllegalDatesException("Illegal dates");
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, REQUEST_TIME_FORMAT);
    }
}