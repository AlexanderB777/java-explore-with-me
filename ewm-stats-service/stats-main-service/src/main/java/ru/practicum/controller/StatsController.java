package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService service;

    @PostMapping
    public void recordHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Controller: record hit() {}", endpointHitDto);
        service.recordHit(endpointHitDto);
    }

    @GetMapping
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam (required = false) List<String> uris,
                                       @RequestParam (required = false, defaultValue = "false") Boolean unique) {
        log.info("Controller: getStats() start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}
