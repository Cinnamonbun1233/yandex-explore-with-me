package ewm.client;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class StatsClient {
    private static final String BASE_URL = "stats-server:9090";
    private final WebClient webClient = WebClient.create();

    public Mono<Void> createNewRecord(StatsRequestDto statsRequestDto) {

        return webClient
                .post()
                .uri(String.format("%s/hit", BASE_URL))
                .bodyValue(statsRequestDto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<StatsResponseDto> getStats(String startPeriod, String endPeriod, List<String> uris, Boolean unique) {

        if (uris == null) {
            return getStatsWithoutUri(startPeriod, endPeriod, unique);
        } else {
            return getStatsWithUri(startPeriod, endPeriod, uris, unique);
        }
    }

    private Flux<StatsResponseDto> getStatsWithoutUri(String startPeriod, String endPeriod, Boolean unique) {

        Optional<Boolean> uniqueOptional = Optional.ofNullable(unique);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", BASE_URL))
                        .queryParam("start", startPeriod)
                        .queryParam("end", endPeriod)
                        .queryParamIfPresent("unique", uniqueOptional)
                        .build())
                .retrieve()
                .bodyToFlux(StatsResponseDto.class);
    }

    private Flux<StatsResponseDto> getStatsWithUri(String startPeriod, String endPeriod, List<String> uris, Boolean unique) {

        Optional<Boolean> uniqueOptional = Optional.ofNullable(unique);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", BASE_URL))
                        .queryParam("start", startPeriod)
                        .queryParam("end", endPeriod)
                        .queryParam("uris", uris)
                        .queryParamIfPresent("unique", uniqueOptional)
                        .build())
                .retrieve()
                .bodyToFlux(StatsResponseDto.class);
    }
}