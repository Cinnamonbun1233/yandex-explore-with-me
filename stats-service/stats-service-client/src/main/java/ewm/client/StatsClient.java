package ewm.client;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class StatsClient {
    private static final String BASE_URL = "stats-server:9090";
    private final WebClient webClient = WebClient.create();

    public Mono<Void> saveRecord(StatsRequestDto statsRequestDto) {
        return webClient
                .post()
                .uri(String.format("%s/hit", BASE_URL))
                .bodyValue(statsRequestDto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<StatsResponseDto> getStats(String start, String end, String[] uris, String unique) {
        if (uris == null) {
            return getStatsWithoutUri(start, end, unique);
        } else {
            return getStatsWithUri(start, end, uris, unique);
        }
    }

    private Flux<StatsResponseDto> getStatsWithUri(String start, String end, String[] uris, String unique) {
        Optional<String> uniqueOpt = Optional.ofNullable(unique);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", BASE_URL))
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParamIfPresent("unique", uniqueOpt)
                        .build())
                .retrieve()
                .bodyToFlux(StatsResponseDto.class);
    }

    private Flux<StatsResponseDto> getStatsWithoutUri(String start, String end, String unique) {
        Optional<String> uniqueOpt = Optional.ofNullable(unique);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", BASE_URL))
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParamIfPresent("unique", uniqueOpt)
                        .build())
                .retrieve()
                .bodyToFlux(StatsResponseDto.class);
    }
}