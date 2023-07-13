package ewm_server.controller;

import ewm_dto.domain_dto.StatsRequestDto;
import ewm_server.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsServerController {
    private final StatsService statsService;

    @Autowired
    public StatsServerController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping(value = "/hits")
    public String hit(StatsRequestDto request) {
        statsService.saveRecord(request);
        return "Stats record saved";
    }
}