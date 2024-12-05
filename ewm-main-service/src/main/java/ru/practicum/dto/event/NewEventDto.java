package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.model.Location;

public record NewEventDto(@NotNull
                          @NotBlank
                          @Size(min = 20, max = 2_000)
                          String annotation,
                          @NotNull
                          Long category,
                          @NotNull
                          @NotBlank
                          @Size(min = 20, max = 7_000)
                          String description,
                          @NotNull
                          @NotBlank
                          String eventDate,
                          @NotNull
                          Location location,
                          Boolean paid,
                          @PositiveOrZero
                          Integer participantLimit,
                          Boolean requestModeration,
                          @NotNull
                          @NotBlank
                          @Size(min = 3, max = 120)
                          String title) {
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