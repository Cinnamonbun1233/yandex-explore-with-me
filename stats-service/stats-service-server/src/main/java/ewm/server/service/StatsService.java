package ewm.server.service;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveRecord(StatsRequestDto statsRequestDto);

    List<StatsResponseDto> getStats(LocalDateTime statsPeriodStartLdt, LocalDateTime statsPeriodEndLdt, String[] uris, String unique);
}