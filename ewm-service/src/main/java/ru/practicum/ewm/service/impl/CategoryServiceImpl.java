package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.exception.DeletionBlockedException;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category;
        try {
            category = categoryRepository.save(mapper.convertCategoryDtoToCategory(categoryDto));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return mapper.convertCategoryToCategoryDto(category);
    }

    @Override
    public void deleteCategoryById(long catId) {
        checkCategoryExists(catId);
        checkCategoryEmpty(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        checkCategoryExists(catId);
        try {
            return mapper.convertCategoryToCategoryDto(
                    categoryRepository.save(new Category(catId, categoryDto.getName())));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }

    }

    private void checkCategoryExists(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ObjectNotFoundException("The required object was not found.", "User with id=" + catId + " was not found", LocalDateTime.now());
        }
    }

    private void checkCategoryEmpty(long catId) {
        if (eventRepository.existsEventByCategoryId(catId)) {
            throw new DeletionBlockedException("For the requested operation the conditions are not met.", "The category is not empty", LocalDateTime.now());
        }
    }


}
