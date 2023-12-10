package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.CategoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final HelperCheckEntity helperCheckEntity;

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
        helperCheckEntity.checkCategoryEmpty(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        checkCategoryExists(catId);
        categoryDto.setId(catId);
        return createCategory(categoryDto);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).stream()
                .map(mapper::convertCategoryToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> getNotFoundException(catId));
        return mapper.convertCategoryToCategoryDto(category);
    }

    private void checkCategoryExists(long catId) {
        if (!categoryRepository.existsById(catId)) {
            getNotFoundException(catId);
        }
    }

    private ObjectNotFoundException getNotFoundException(long id) {
        throw new ObjectNotFoundException("The required object was not found.", "Category with id=" + id + " was not found", LocalDateTime.now());
    }


}
