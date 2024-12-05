package ru.practicum.dto.event;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.model.EventStateAction;
import ru.practicum.model.Location;

public record UpdateEventAdminRequest(@Size(min = 20, max = 2_000)
                                      String annotation,
                                      Long category,
                                      @Size(min = 20, max = 7_000)
                                      String description,
                                      String eventDate,
                                      Location location,
                                      Boolean paid,
                                      Integer participantLimit,
                                      Boolean requestModeration,
                                      EventStateAction stateAction,
                                      @Size(min = 3, max = 120)
                                      String title) {
}