package ewm_server.service;

import ewm_dto.domain_dto.StatsRequestDto;
import ewm_dto.domain_dto.StatsResponseDto;
import ewm_server.mapper.StatsMapper;
import ewm_server.model.StatsRecord;
import ewm_server.repo.StatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {
    private final static DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepo statsRepo;

    @Autowired
    public StatsServiceImpl(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    @Override
    public void saveRecord(StatsRequestDto request) {
        statsRepo.save(StatsMapper.mapRequestToModel(request));
    }

    @Override
    public List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique) {
        List<StatsRecord> recordsFound;
        if (uris == null) {
            recordsFound = statsRepo.findAllByTimestampBetween(parseDateTime(start), parseDateTime(end));
        } else {
            recordsFound = statsRepo.findAllByTimestampBetweenAndUriIn(parseDateTime(start),
                    parseDateTime(end), uris);
        }
        if (unique == null) {
            Map<String, Long> hitsForUriMap = recordsFound.stream()
                    .collect(Collectors.groupingBy(StatsRecord::getUri, Collectors.counting()));
            return recordsFound.stream().map(r -> new StatsResponseDto(r.getApp(), r.getUri(),
                            hitsForUriMap.get(r.getUri())))
                    .distinct()
                    .sorted(Comparator.comparing(StatsResponseDto::getHits).reversed())
                    .collect(Collectors.toList());
        } else {
            Map<String, Map<String, Long>> hitsForUriAndIpMap = recordsFound.stream()
                    .collect(Collectors.groupingBy(StatsRecord::getUri,
                            Collectors.groupingBy(StatsRecord::getIp, Collectors.counting())));
            return recordsFound.stream().map(r -> new StatsResponseDto(r.getApp(), r.getUri(),
                            Long.valueOf(hitsForUriAndIpMap.get(r.getUri()).size())))
                    .distinct()
                    .sorted(Comparator.comparing(StatsResponseDto::getHits).reversed())
                    .collect(Collectors.toList());
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, REQUEST_TIME_FORMAT);
    }
}