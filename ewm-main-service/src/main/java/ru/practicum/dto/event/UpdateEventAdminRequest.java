package ru.practicum.dto.event;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.model.EventStateAction;
import ru.practicum.model.Location;

@Data
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2_000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7_000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}