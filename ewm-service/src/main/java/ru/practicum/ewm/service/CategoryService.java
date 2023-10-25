package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategoryById(long catId);

    CategoryDto updateCategory(long cat, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);
}
