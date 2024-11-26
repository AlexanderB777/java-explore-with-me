package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.model.Location;

@Data
public class NewEventDto {
    @NotNull
    @NotBlank
    @Size(min = 20, max = 2_000)
    private String annotation;
    @NotNull
    private Long category;
    @NotNull
    @NotBlank
    @Size(min = 20, max = 7_000)
    private String description;
    @NotNull
    @NotBlank
    private String eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotNull
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @Override
    public String toString() {
        return "NewEventDto [annotation=" +
                annotation.length() +
                ", categoryId=" +
                category +
                ", description=" +
                description.length() +
                ", eventDate=" +
                eventDate +
                ", location=" +
                location +
                ", paid=" +
                paid +
                ", participantLimit=" +
                participantLimit +
                ", requestModeration=" +
                requestModeration +
                ", title=" +
                title +
                "]";
    }
}