package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.service.EventsService;
import ru.practicum.service.RequestsService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class PrivateEventsController {
    private final EventsService service;
    private final RequestsService requestsService;

    @GetMapping
    public List<EventShortDto> getUsersEvents(@PathVariable @Positive Long userId,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Controller: getUsersEvents() userId={}, from={}, size={}", userId, from, size);
        return service.getUsersEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createUsersEvent(@PathVariable Long userId,
                                         @RequestBody @Valid NewEventDto dto) {
        log.info("Controller: createUsersEvent() userId={}, dto={}", userId, dto);
        return service.createUsersEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUsersEventById(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("Controller: getUsersEventById() userId={}, eventId={}", userId, eventId);
        return service.getUsersEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUsersEventById(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @RequestBody @Valid UpdateEventUserRequest request) {
        log.info("Controller: updateUsersEventById() userId={}, eventId={}, request={}", userId, eventId, request);
        return service.updateUsersEventById(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUsersParticipationRequests(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        log.info("Controller: getUsersParticipationRequests() userId={}, eventId={}", userId, eventId);
        return service.getUsersParticipationRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUsersParticipationRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest request
    ) {
        log.info("Controller: updateUsersParticipationRequest() userId={}, eventId={}, request={}",
                userId, eventId, request);
        return service.updateUsersParticipationRequest(userId, eventId, request);
    }
}