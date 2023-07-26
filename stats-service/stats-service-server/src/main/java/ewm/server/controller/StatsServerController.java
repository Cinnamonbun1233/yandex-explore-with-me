package ewm.server.controller;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import ewm.server.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class StatsServerController {
    private final StatsService statsService;

    @Autowired
    public StatsServerController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping(value = "/hit")
    public ResponseEntity<Void> addHit(@RequestBody StatsRequestDto statsRequestDto, HttpServletRequest httpServletRequest) {
        statsService.saveRecord(statsRequestDto, httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsResponseDto>> getStats(@RequestParam("start") String start,
                                                           @RequestParam("end") String end,
                                                           @RequestParam(value = "uris", required = false) List<String> uris,
                                                           @RequestParam(value = "unique", required = false) Boolean unique) {
        return ResponseEntity.ok().body(statsService.getStats(start, end, uris, unique));
    }
}