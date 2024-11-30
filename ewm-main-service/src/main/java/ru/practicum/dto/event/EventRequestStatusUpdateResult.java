package ru.practicum.dto.event;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public record EventRequestStatusUpdateResult(List<ParticipationRequestDto> confirmedRequests,
                                             List<ParticipationRequestDto> rejectedRequests) {}