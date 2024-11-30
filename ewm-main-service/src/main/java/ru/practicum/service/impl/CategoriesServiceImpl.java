package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoriesMapper;
import ru.practicum.reposirory.CategoriesRepository;
import ru.practicum.service.CategoriesService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository repository;
    private final CategoriesMapper mapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        log.info("Service: addCategory(), categoryDto: {}", categoryDto);
        return saveDto(categoryDto);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Service: deleteCategory(), id: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("Category with id: " + id + " not found");
        }
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        log.info("Service: updateCategory(), id: {}, categoryDto = {}", id, categoryDto);
        if (repository.existsById(id)) {
            return saveDto(new CategoryDto(id, categoryDto.name()));
        } else {
            throw new NotFoundException("Category with id: " + id + " not found");
        }
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Service: getCategories(), from: {}, size: {}", from, size);
        return mapper.toDto(repository
                .findAll(PageRequest.of(from, size))
                .getContent());
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        log.info("Service: getCategoryById(), catId: {}", catId);
        return repository.findById(catId.longValue())
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("Category with id: " + catId + " not found"));
    }

    private CategoryDto saveDto(CategoryDto dto) {
        Category category = mapper.toEntity(dto);
        category = repository.save(category);
        return mapper.toDto(category);
    }
}