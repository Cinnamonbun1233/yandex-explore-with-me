package ewm_server.service;

import ewm_dto.domain_dto.StatsRequestDto;
import ewm_server.model.StatsRecord;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {
    @Override
    public void saveRecord(StatsRequestDto request) {
        //NOTE: when record is new - save record with 1 hit
        //NOTE: when record is created - increment hits
    }
}