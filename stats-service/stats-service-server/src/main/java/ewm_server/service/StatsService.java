package ewm_server.service;

import ewm_dto.StatsRequestDto;
import ewm_dto.StatsResponseDto;

import java.util.List;

public interface StatsService {
    void saveRecord(StatsRequestDto request);

    List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique);
}