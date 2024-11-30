package ru.practicum.controller.adm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.model.EventState;
import ru.practicum.service.EventsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
public class AdminEventsController {
    private final EventsService service;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0")
                                            @PositiveOrZero Integer from,
                                        @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Controller: getEvents(), users = {}, states = {}, categories = {}, rangeStart = {}, " +
                "rangeEnd = {}, from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest request) {
        log.info("Controller: updateEvent(), eventId = {}, request = {}", eventId, request);
        return service.updateEvent(eventId, request);
    }
}