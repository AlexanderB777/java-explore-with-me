package ru.practicum.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
}