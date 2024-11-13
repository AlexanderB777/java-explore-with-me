package ru.practicum.stats;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {
    private static final String BASE_URL = "http://localhost:9090";
    private final WebClient webClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient() {
        this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
    }

    public Mono<Void> recordHit(String app,
                                String uri,
                                String ip,
                                LocalDateTime timestamp) {
        EndpointHitDto dto = new EndpointHitDto(app, uri, ip, timestamp.format(formatter));

        return webClient.post()
                .uri("/hit")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<List<ViewStatsDto>> getStats(LocalDateTime start,
                                             LocalDateTime end,
                                             List<String> uris,
                                             Boolean unique) {

        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/stats")
                .queryParam("start", encodeTime(start))
                .queryParam("end", encodeTime(end))
                .build()
                .encode()
                .toUri();

        if (uris != null && uris.isEmpty()) {
            uri = UriComponentsBuilder.fromUri(uri)
                    .queryParam("uris", uris)
                    .build()
                    .encode()
                    .toUri();
        }

        if (Boolean.TRUE.equals(unique)) {
            uri = UriComponentsBuilder.fromUri(uri)
                    .queryParam("unique", true)
                    .build()
                    .encode()
                    .toUri();
        }

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ViewStatsDto[].class)
                .map(List::of);
    }

    public Mono<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, null);
    }

    public Mono<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, null);
    }

    public Mono<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return getStats(start, end, null, unique);
    }


    private String encodeTime(LocalDateTime time) {
        return URLEncoder.encode(time.format(formatter), StandardCharsets.UTF_8);
    }
}