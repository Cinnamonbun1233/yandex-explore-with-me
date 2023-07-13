package ewm_server.service;

import ewm_dto.domain_dto.StatsRequestDto;

public interface StatsService {
    void saveRecord(StatsRequestDto request);
}