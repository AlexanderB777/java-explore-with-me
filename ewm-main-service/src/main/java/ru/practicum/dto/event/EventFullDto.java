package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

public record EventFullDto(String annotation, CategoryDto category, Long confirmedRequests, String createdOn,
                           String description, String eventDate, Long id, UserShortDto initiator, Location location,
                           Boolean paid, Integer participantLimit, String publishedOn, Boolean requestModeration,
                           EventState state, String title, Long views, Long likes, Long dislikes) {
}