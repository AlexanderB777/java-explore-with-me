package ru.practicum.dto.compilation;

import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.io.Serializable;
import java.util.List;

public record CompilationDto(List<EventShortDto> events,
                             Long id,
                             Boolean pinned,
                             String title) {}