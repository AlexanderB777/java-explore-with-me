package ru.practicum.dto.event;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

public record EventShortDto(String annotation,
                            CategoryDto category,
                            Long confirmedRequests,
                            String eventDate,
                            Long id,
                            UserShortDto initiator,
                            Boolean paid,
                            String title,
                            Long views,
                            Long likes,
                            Long dislikes) {}