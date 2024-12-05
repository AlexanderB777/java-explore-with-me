package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewCompilationDto(List<Long> events,
                                Boolean pinned,
                                @NotNull
                                @NotBlank
                                @Size(min = 1, max = 50)
                                String title) {}