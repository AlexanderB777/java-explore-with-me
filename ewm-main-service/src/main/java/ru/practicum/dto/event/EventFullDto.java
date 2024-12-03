package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

@Data
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;
    private Long likes;
    private Long dislikes;
}