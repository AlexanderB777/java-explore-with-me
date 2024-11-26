package ru.practicum.dto;

import lombok.Data;
import ru.practicum.model.ParticipationRequestStatus;

@Data
public class ParticipationRequestDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private ParticipationRequestStatus status;
}