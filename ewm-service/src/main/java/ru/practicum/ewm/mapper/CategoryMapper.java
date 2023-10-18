package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.Category;

@Component
public class CategoryMapper {

    public CategoryDto convertCategoryToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDto(category.getId(), category.getName());
    }

    public Category convertCategoryDtoToCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }
}
