package ewm.server.service;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatsService {
    void saveRecord(StatsRequestDto statsRequestDto, HttpServletRequest httpServletRequest);

    List<StatsResponseDto> getStats(String start, String end, List<String> uris, Boolean unique);
}