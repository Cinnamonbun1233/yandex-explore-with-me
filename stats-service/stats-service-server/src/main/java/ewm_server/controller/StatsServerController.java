package ewm_server.controller;

import ewm_dto.domain_dto.StatsRequestDto;
import ewm_dto.domain_dto.StatsResponseDto;
import ewm_server.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatsServerController {
    private final StatsService statsService;

    @Autowired
    public StatsServerController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping(value = "/hit")
    public String addHit(@RequestBody StatsRequestDto request) {
        statsService.saveRecord(request);
        return "Stats record saved";
    }

    @GetMapping("/stats")
    public List<StatsResponseDto> getStats(@RequestParam("start") String start,
                                           @RequestParam("end") String end,
                                           @RequestParam(value = "uris", required = false) String[] uris,
                                           @RequestParam(value = "unique", required = false) String unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}