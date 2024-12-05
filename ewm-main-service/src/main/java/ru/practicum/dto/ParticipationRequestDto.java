package ru.practicum.dto;

import lombok.Data;
import ru.practicum.model.ParticipationRequestStatus;

public record ParticipationRequestDto(String created,
                                      Long event,
                                      Long id,
                                      Long requester,
                                      ParticipationRequestStatus status) {
}