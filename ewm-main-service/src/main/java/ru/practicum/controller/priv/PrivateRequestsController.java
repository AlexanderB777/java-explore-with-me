package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class PrivateRequestsController {
    private final RequestsService service;

    @GetMapping
    public List<ParticipationRequestDto> getUsersRequests(@PathVariable Long userId) {
        log.info("Controller: getUsersRequests(), userId = {}", userId);
        return service.getUsersRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUsersRequest(@PathVariable Long userId,
                                                      @RequestParam Long eventId) {
        log.info("Controller: createUsersRequest(), userId = {}, eventId = {}", userId, eventId);
        return service.createUsersRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUsersRequest(@PathVariable Long userId,
                                                      @PathVariable Long requestId) {
        log.info("Controller: cancelUsersRequest(), userId = {}, requestId = {}", userId, requestId);
        return service.cancelUsersRequest(userId, requestId);
    }
}