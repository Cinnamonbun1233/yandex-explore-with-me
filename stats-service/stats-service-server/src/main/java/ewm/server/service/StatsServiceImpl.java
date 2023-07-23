package ewm.server.service;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import ewm.server.exception.IllegalDatesException;
import ewm.server.mapper.StatsMapper;
import ewm.server.repo.StatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepo statsRepo;

    @Autowired
    public StatsServiceImpl(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    @Override
    @Transactional
    public void saveRecord(StatsRequestDto statsRequestDto, HttpServletRequest httpServletRequest) {
        statsRequestDto.setIp(httpServletRequest.getRemoteAddr());
        statsRepo.save(StatsMapper.mapRequestToModel(statsRequestDto));
    }

    @Override
    public List<StatsResponseDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        validateDates(start, end);
        if (Boolean.TRUE.equals(unique)) {
            return statsRepo.getStatsForDatesAndUrisWithUniqueIp(parseDateTime(start), parseDateTime(end), uris);
        } else {
            return statsRepo.getStatsForDatesAndUris(parseDateTime(start), parseDateTime(end), uris);
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