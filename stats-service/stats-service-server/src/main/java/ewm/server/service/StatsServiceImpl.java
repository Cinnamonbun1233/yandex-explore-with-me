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
    public void createNewRecord(StatsRequestDto statsRequestDto, HttpServletRequest httpServletRequest) {

        statsRequestDto.setIp(httpServletRequest.getRemoteAddr());
        statsRepo.save(StatsMapper.statsRequestDtoToStatsRecord(statsRequestDto));
    }

    @Override
    public List<StatsResponseDto> getStats(String startPeriod, String endPeriod, List<String> uris, Boolean unique) {

        validateDates(startPeriod, endPeriod);

        if (Boolean.TRUE.equals(unique)) {
            return statsRepo.getStatsForDatesAndUrisWithUniqueIp(parseDateTime(startPeriod), parseDateTime(endPeriod), uris);
        } else {
            return statsRepo.getStatsForDatesAndUris(parseDateTime(startPeriod), parseDateTime(endPeriod), uris);
        }
    }

    private void validateDates(String startPeriod, String endPeriod) {

        if (parseDateTime(startPeriod).isAfter(parseDateTime(endPeriod))) {
            throw new IllegalDatesException("Illegal dates");
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {

        return LocalDateTime.parse(dateTime, REQUEST_TIME_FORMAT);
    }
}