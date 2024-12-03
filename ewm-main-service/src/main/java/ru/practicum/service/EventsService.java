package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.model.EventState;

import java.util.List;

public interface EventsService {
    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto createUsersEvent(Long userId, NewEventDto dto);

    EventFullDto getUsersEventById(Long userId, Long eventId);

    EventFullDto updateUsersEventById(Long userId, Long eventId, UpdateEventUserRequest request);

    List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                 String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size, String remoteAddr, String requestURI);

    EventFullDto getEvent(Long id, String remoteAddr, String requestURI);

    List<ParticipationRequestDto> getUsersParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUsersParticipationRequest(Long userId,
                                                                   Long eventId,
                                                                   EventRequestStatusUpdateRequest request);

    EventShortDto putLike(Long userId, Long eventId);

    EventShortDto putDislike(Long userId, Long eventId);

    void deleteLike(Long userId, Long eventId);

    void deleteDislike(Long userId, Long eventId);

    List<EventFullDto> getEventsByRating();
}