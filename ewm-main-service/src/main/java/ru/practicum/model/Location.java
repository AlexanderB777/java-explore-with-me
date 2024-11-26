package ru.practicum.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record Location(@NotNull
                       @Positive
                       float lat,
                       @Positive
                       @NotNull
                       float lon) {}