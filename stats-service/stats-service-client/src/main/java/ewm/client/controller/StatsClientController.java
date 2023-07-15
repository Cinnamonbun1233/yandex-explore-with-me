package ewm.client.controller;

import ewm.client.client.StatsClient;
import ewm.dto.DateTimeFormatterFactory;
import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
public class StatsClientController {
    private final StatsClient statsClient;

    @Autowired
    public StatsClientController(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    @PostMapping(value = "/hit")
    public Mono<Void> addHit(@RequestBody StatsRequestDto statsRequestDto) {
        return statsClient.saveRecord(statsRequestDto);
    }

    @GetMapping("/stats")
    public Flux<StatsResponseDto> getStats(@RequestParam("start") String statsPeriodStart,
                                           @RequestParam("end") String statsPeriodEnd,
                                           @RequestParam(value = "uris", required = false) String[] uris,
                                           @RequestParam(value = "unique", required = false) String unique) {
        LocalDateTime statsPeriodStartLdt = LocalDateTime.parse(statsPeriodStart, DateTimeFormatterFactory.getDefaultDateTimeFormatter());
        LocalDateTime statsPeriodEndLdt = LocalDateTime.parse(statsPeriodEnd, DateTimeFormatterFactory.getDefaultDateTimeFormatter());
        return statsClient.getStats(statsPeriodStartLdt, statsPeriodEndLdt, uris, unique);
    }
}