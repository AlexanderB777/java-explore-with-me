package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.reposirory.EventsRepository;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {EventMapper.class, CategoriesMapper.class, UserMapper.class})
public interface CompilationsMapper {

    @Mapping(target = "events", source = "events", qualifiedByName = "mapIdsToEvents")
    Compilation toEntity(NewCompilationDto dto, @Context EventsRepository eventsRepository);

    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation);

    @IterableMapping(elementTargetType = CompilationDto.class)
    List<CompilationDto> toDto(List<Compilation> compilations);

    @Named("mapIdsToEvents")
    default List<Event> mapIdsToEvents(List<Long> ids, @Context EventsRepository eventsRepository) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .map(eventsRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}