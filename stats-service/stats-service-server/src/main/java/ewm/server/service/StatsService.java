package ewm.server.service;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatsService {
    void saveRecord(StatsRequestDto request, HttpServletRequest meta);

    List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique);
}