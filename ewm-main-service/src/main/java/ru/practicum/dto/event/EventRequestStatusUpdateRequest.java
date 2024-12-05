package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.model.RequestStatus;

import java.util.List;

public record EventRequestStatusUpdateRequest(List<Long> requestIds,
                                              RequestStatus status) {
}