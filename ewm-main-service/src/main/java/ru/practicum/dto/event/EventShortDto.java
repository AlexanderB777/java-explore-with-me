package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

@Data
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}