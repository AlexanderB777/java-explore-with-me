package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.CategoryDto;
import ru.practicum.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriesMapper {
    Category toEntity(CategoryDto categoryDto);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(List<Category> categories);
}
