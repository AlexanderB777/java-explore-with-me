package ru.practicum.stats;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {
    private static final String BASE_URL = "http://stats-server:9090";
    private final WebClient webClient = WebClient.builder().baseUrl(BASE_URL).build();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void recordHit(String app,
                          String uri,
                          String ip,
                          LocalDateTime timestamp) {
        EndpointHitDto dto = new EndpointHitDto(app, uri, ip, timestamp.format(FORMATTER));

        webClient.post()
                .uri("/hit")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(void.class)
                .block();
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                             LocalDateTime end,
                                             List<String> uris,
                                             Boolean unique) {

        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .encode()
                .build().toUri();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ViewStatsDto[].class)
                .map(List::of)
                .block();
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, null, null);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, null);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return getStats(start, end, null, unique);
    }
}