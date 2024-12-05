package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

public record UpdateCompilationRequest(List<Long> events,
                                       Boolean pinned,
                                       @Size(min = 1, max = 50)
                                       String title) {}