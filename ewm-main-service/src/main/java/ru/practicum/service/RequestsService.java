package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestsService {

    List<ParticipationRequestDto> getUsersRequests(Long userId);

    ParticipationRequestDto createUsersRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUsersRequest(Long userId, Long requestId);
}