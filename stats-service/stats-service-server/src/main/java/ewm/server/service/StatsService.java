package ewm.server.service;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;

import java.util.List;

public interface StatsService {
    void saveRecord(StatsRequestDto request);

    List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique);
}