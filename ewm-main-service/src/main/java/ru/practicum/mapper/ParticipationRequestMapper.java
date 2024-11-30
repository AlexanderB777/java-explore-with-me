package ru.practicum.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.util.UtilConstants;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "created",
            source = "created",
            qualifiedByName = "timeToString")
    @Mapping(target = "event",
            source = "request.event.id")
    @Mapping(target = "requester",
            source = "request.requester.id")
    ParticipationRequestDto toDto(ParticipationRequest request);

    @IterableMapping(elementTargetType = ParticipationRequestDto.class)
    List<ParticipationRequestDto> toDto(List<ParticipationRequest> requests);


    @Named("timeToString")
    static String timeToString(LocalDateTime time) {
        return time.format(UtilConstants.FORMATTER);
    }
}