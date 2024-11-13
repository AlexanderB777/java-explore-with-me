package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit map(EndpointHitDto dto);
}