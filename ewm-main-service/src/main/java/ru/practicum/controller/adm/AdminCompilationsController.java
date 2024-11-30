package ru.practicum.controller.adm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.CompilationsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class AdminCompilationsController {
    private final CompilationsService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Controller: addCompilation(), dto = {}", dto);
        return service.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Controller: deleteCompilation(), compId = {}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest dto) {
        log.info("Controller: updateCompilation(), compId = {}, request = {}", compId, dto);
        return service.updateCompilation(compId, dto);
    }
}