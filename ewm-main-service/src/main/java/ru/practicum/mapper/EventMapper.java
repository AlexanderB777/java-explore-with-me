package ru.practicum.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.entity.*;
import ru.practicum.model.ParticipationRequestStatus;
import ru.practicum.util.UtilConstants;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoriesMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "confirmedRequests",
            expression = "java(countConfirmedRequests(event.getRequests()))")
    @Mapping(target = "eventDate",
            source = "eventDate",
            qualifiedByName = "timeToString")
    @Mapping(target = "likes",
            source = "reactions",
            qualifiedByName = "countLikes")
    @Mapping(target = "dislikes",
            source = "reactions",
            qualifiedByName = "countDislikes")
    EventShortDto toShortDto(Event event);

    @IterableMapping(elementTargetType = EventShortDto.class)
    List<EventShortDto> toShortDto(List<Event> events);

    @Mapping(target = "category",
            source = "dto.category",
            qualifiedByName = "mapCategoryIdToCategory")
    @Mapping(target = "initiator",
            source = "userId",
            qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "lat", source = "dto.location.lat")
    @Mapping(target = "lon", source = "dto.location.lon")
    @Mapping(target = "eventDate",
            source = "dto.eventDate",
            qualifiedByName = "stringToTime")
    @Mapping(target = "views", expression = "java(0L)")
    @Mapping(target = "state", expression = "java(ru.practicum.model.EventState.PENDING)")
    Event toEntity(Long userId, NewEventDto dto);

    @Mapping(target = "confirmedRequests",
            expression = "java(countConfirmedRequests(event.getRequests()))")
    @Mapping(source = "createdOn",
            target = "createdOn",
            qualifiedByName = "timeToString")
    @Mapping(target = "eventDate",
            source = "eventDate",
            qualifiedByName = "timeToString")
    @Mapping(target = "location",
            expression = "java(new Location(event.getLat(), event.getLon()))")
    @Mapping(target = "publishedOn",
            source = "publishedOn",
            qualifiedByName = "timeToString")
    @Mapping(target = "likes",
            source = "reactions",
            qualifiedByName = "countLikes")
    @Mapping(target = "dislikes",
            source = "reactions",
            qualifiedByName = "countDislikes")
    EventFullDto toFullDto(Event event);

    @IterableMapping(elementTargetType = EventFullDto.class)
    List<EventFullDto> toFullDto(List<Event> events);

    default Long countConfirmedRequests(List<ParticipationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return 0L;
        }
        return requests
                .stream()
                .filter(request -> request.getStatus() == ParticipationRequestStatus.CONFIRMED)
                .count();
    }

    @Named("countLikes")
    static Long countLikes(List<Reaction> reactions) {
        if (reactions == null || reactions.isEmpty()) {
            return 0L;
        } else {
            return reactions.stream().filter(Reaction::getIsPositive).count();
        }
    }

    @Named("countDislikes")
    static Long countDislikes(List<Reaction> reactions) {
        if (reactions == null || reactions.isEmpty()) {
            return 0L;
        } else {
            return reactions.stream().filter(reaction -> !reaction.getIsPositive()).count();
        }
    }

    @Named("timeToString")
    static String timeToString(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(UtilConstants.FORMATTER);
    }

    @Named("stringToTime")
    static LocalDateTime stringToTime(String time) {
        return LocalDateTime.parse(time, UtilConstants.FORMATTER);
    }

    @Named("mapCategoryIdToCategory")
    default Category mapCategoryIdToCategory(Long catId) {
        if (catId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(catId);
        return category;
    }

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}