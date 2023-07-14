package ewm_client.controller;

import ewm_client.client.StatsClient;
import ewm_dto.domain_dto.StatsRequestDto;
import ewm_dto.domain_dto.StatsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class StatsClientController {
    private final StatsClient statsClient;

    @Autowired
    public StatsClientController(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    @PostMapping(value = "/hit")
    public Mono<Void> addHit(@RequestBody StatsRequestDto request) {
        return statsClient.saveRecord(request);
    }

    @GetMapping("/stats")
    public Flux<StatsResponseDto> getStats(@RequestParam("start") String start,
                                           @RequestParam("end") String end,
                                           @RequestParam(value = "uris", required = false) String[] uris,
                                           @RequestParam(value = "unique", required = false) String unique) {
        return statsClient.getStats(start, end, uris, unique);
    }
}