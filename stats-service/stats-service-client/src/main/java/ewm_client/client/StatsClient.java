package ewm_client.client;

import ewm_dto.domain_dto.StatsRequestDto;
import ewm_dto.domain_dto.StatsResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class StatsClient {
    private final WebClient client = WebClient.create();
    @Value("${stats-service-server.url}")
    private String baseUrl;

    public Mono<Void> saveRecord(StatsRequestDto request) {
        return client.post()
                .uri(String.format("%s/hit", baseUrl))
                .bodyValue(request)
                .retrieve().bodyToMono(Void.class);
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
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", baseUrl))
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParamIfPresent("unique", uniqueOpt)
                        .build())
                .retrieve().bodyToFlux(StatsResponseDto.class);
    }

    private Flux<StatsResponseDto> getStatsWithoutUri(String start, String end, String unique) {
        Optional<String> uniqueOpt = Optional.ofNullable(unique);
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s/stats", baseUrl))
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParamIfPresent("unique", uniqueOpt)
                        .build())
                .retrieve().bodyToFlux(StatsResponseDto.class);
    }
}