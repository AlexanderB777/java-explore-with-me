package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.model.Location;
import ru.practicum.model.StateAction;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateEventUserRequest(@Size(min = 20, max = 2000)
                                     String annotation,
                                     Long category,
                                     @Size(min = 20, max = 7000)
                                     String description,
                                     String eventDate,
                                     Location location,
                                     Boolean paid,
                                     @PositiveOrZero
                                     Integer participantLimit,
                                     Boolean requestModeration,
                                     StateAction stateAction,
                                     @Size(min = 3, max = 120)
                                     String title) {
}