package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoriesService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Controller: getCategories() from = {}, size = {}", from, size);
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Integer catId) {
        log.info("Controller: getCategory() catId = {}", catId);
        return service.getCategoryById(catId);
    }
}