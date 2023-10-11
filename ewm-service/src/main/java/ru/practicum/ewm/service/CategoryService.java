package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategoryById(long catId);

    CategoryDto updateCategory(long cat, CategoryDto categoryDto);
}
