package ru.practicum.dto;

import ru.practicum.model.ParticipationRequestStatus;

public record ParticipationRequestDto(String created,
                                      Long event,
                                      Long id,
                                      Long requester,
                                      ParticipationRequestStatus status) {
}