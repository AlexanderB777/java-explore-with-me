package ru.practicum.dto.compilation;

import ru.practicum.dto.event.EventShortDto;

import java.util.List;

public record CompilationDto(List<EventShortDto> events,
                             Long id,
                             Boolean pinned,
                             String title) {}