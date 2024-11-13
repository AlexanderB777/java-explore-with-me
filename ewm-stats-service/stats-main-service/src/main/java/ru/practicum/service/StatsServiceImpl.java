package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;
    private final EndpointHitMapper mapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void recordHit(EndpointHitDto endpointHitDto) {
        log.info("Service: recordHit() {}", endpointHitDto);
        repository.save(mapper.map(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("Service: getStats() start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);

        List<EndpointHit> fromRepo = (uris == null || uris.isEmpty())
                ? repository.findByTimestampBetween(decodeTimeString(start), decodeTimeString(end))
                : repository.findByTimestampBetweenAndUriIn(decodeTimeString(start), decodeTimeString(end), uris);


        return unique
                ? fromRepo.stream()
                .collect(Collectors.groupingBy(
                        x -> Map.entry(x.getApp(), x.getUri()), Collectors.counting()
                ))
                .entrySet().stream()
                .map(x -> new ViewStatsDto(
                        x.getKey().getKey(),
                        x.getKey().getValue(),
                        x.getValue()))
                .collect(Collectors.toList())

                : fromRepo.stream()
                .collect(Collectors.groupingBy(
                        x -> Map.entry(x.getApp(), x.getUri()),
                        Collectors.mapping(
                                EndpointHit::getIp,
                                Collectors.toSet()
                        )
                ))
                .entrySet().stream()
                .map(x -> new ViewStatsDto(
                        x.getKey().getKey(),
                        x.getKey().getValue(),
                        (long) x.getValue().size()))
                .collect(Collectors.toList());
    }

    private LocalDateTime decodeTimeString(String timeString) {
        return LocalDateTime.parse(URLDecoder.decode(timeString, StandardCharsets.UTF_8), formatter);
    }
}