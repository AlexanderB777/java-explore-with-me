package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.TimeException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;
    private final EndpointHitMapper mapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void recordHit(EndpointHitDto endpointHitDto) {
        log.info("Service: recordHit() {}", endpointHitDto);
        repository.save(mapper.map(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("Service: getStats() start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);

        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);

        if (endTime.isBefore(startTime)) {
            throw new TimeException("End time is before start time");
        }

        List<EndpointHit> fromRepo = (uris == null || uris.isEmpty())
                ? repository.findByTimestampBetween(startTime, endTime)
                : repository.findByTimestampBetweenAndUriIn(startTime, endTime, uris);

        return unique
                ? generateResponseWithUniqueTrue(fromRepo)
                : generateResponseWithUniqueFalse(fromRepo);
    }

    private List<ViewStatsDto> generateResponseWithUniqueTrue(List<EndpointHit> endpointHits) {
        return endpointHits.stream()
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
                .sorted(Comparator.comparing(ViewStatsDto::hits).reversed())
                .toList();
    }

    private List<ViewStatsDto> generateResponseWithUniqueFalse(List<EndpointHit> endpointHits) {
        return endpointHits.stream()
                .collect(Collectors.groupingBy(
                        x -> Map.entry(x.getApp(), x.getUri()), Collectors.counting()
                ))
                .entrySet().stream()
                .map(x -> new ViewStatsDto(
                        x.getKey().getKey(),
                        x.getKey().getValue(),
                        x.getValue()))
                .sorted(Comparator.comparing(ViewStatsDto::hits).reversed())
                .toList();
    }
}