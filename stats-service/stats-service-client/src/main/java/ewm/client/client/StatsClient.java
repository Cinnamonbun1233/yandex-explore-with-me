package ewm.client.client;

import ewm.dto.StatsRequestDto;
import ewm.dto.StatsResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StatsClient {
    @Value("${stats-service-server.url}")
    private String baseUrl;
    private final WebClient webClient = WebClient.create();

    public Mono<Void> saveRecord(StatsRequestDto statsRequestDto) {
        return webClient
                .post()
                .uri(String.format("%s/hit", baseUrl))
                .bodyValue(statsRequestDto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<StatsResponseDto> getStats(LocalDateTime statsPeriodStartLdt, LocalDateTime statsPeriodEndLdt, String[] uris, String unique) {
        if (uris == null) {
            return getStatsWithoutUri(statsPeriodStartLdt, statsPeriodEndLdt, unique);
        } else {
            return getStatsWithUri(statsPeriodStartLdt, statsPeriodEndLdt, uris, unique);
        }
    }

    private Flux<StatsResponseDto> getStatsWithUri(LocalDateTime statsPeriodStartLdt, LocalDateTime statsPeriodEndLdt, String[] uris, String unique) {
        Optional<String> uniqueOpt = Optional.ofNullable(unique);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", baseUrl))
                        .queryParam("start", statsPeriodStartLdt)
                        .queryParam("end", statsPeriodEndLdt)
                        .queryParam("uris", uris)
                        .queryParamIfPresent("unique", uniqueOpt)
                        .build())
                .retrieve()
                .bodyToFlux(StatsResponseDto.class);
    }

    private Flux<StatsResponseDto> getStatsWithoutUri(LocalDateTime statsPeriodStartLdt, LocalDateTime statsPeriodEndLdt, String unique) {
        Optional<String> uniqueOpt = Optional.ofNullable(unique);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", baseUrl))
                        .queryParam("start", statsPeriodStartLdt)
                        .queryParam("end", statsPeriodEndLdt)
                        .queryParamIfPresent("unique", uniqueOpt)
                        .build())
                .retrieve()
                .bodyToFlux(StatsResponseDto.class);
    }
}