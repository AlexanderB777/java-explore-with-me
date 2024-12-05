package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationsMapper;
import ru.practicum.reposirory.CompilationsRepository;
import ru.practicum.reposirory.EventsRepository;
import ru.practicum.service.CompilationsService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationsService {
    private final CompilationsRepository repository;
    private final EventsRepository eventsRepository;
    private final CompilationsMapper mapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("service: getCompilations(), pinned = {}, from = {}, size = {}", pinned, from, size);
        Pageable pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations = (pinned == null)
                ? repository.findAll(pageRequest).getContent()
                : repository.findAllByPinned(pinned, pageRequest).getContent();
        return mapper.toDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(long id) {
        log.info("service: getCompilationById(), id = {}", id);
        return mapper
                .toDto(repository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException(String
                                .format("Compilation with id=%d was not found", id))));
    }



    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {
        log.info("service: addCompilation(), dto = {}", dto.toString());
        Compilation compilation = mapper.toEntity(dto, eventsRepository);
        if (dto.pinned() == null) {
            compilation.setPinned(false);
        }
        return mapper.toDto(repository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        log.info("service: deleteCompilation(), compId = {}", compId);
        if (!repository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        } else {
            repository.deleteById(compId);
        }
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        log.info("service: updateCompilation(), compId = {}, dto = {}", compId, dto.toString());
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Compilation with id=%d was not found", compId)));
        if (dto.pinned() != null) {
            compilation.setPinned(dto.pinned());
        }
        if (dto.title() != null) {
            compilation.setTitle(dto.title());
        }
        compilation.setEvents(new ArrayList<>());
        if (dto.events() != null && !dto.events().isEmpty()) {
            for (Long eventId : dto.events()) {
                if (!eventsRepository.existsById(eventId)) {
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                }
                Event event = new Event();
                event.setId(eventId);
                compilation.getEvents().add(event);
            }
        }
        return mapper.toDto(repository.save(compilation));
    }
}