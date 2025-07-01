package com.equipment.tracker.service;

import com.equipment.tracker.entity.Category;
import com.equipment.tracker.exception.BadRequestException;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(@Valid Category category) {
        validateCategory(category);
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new BadRequestException("Категория должна быть уникальной");
        }
        Category saved = categoryRepository.save(category);
        logger.info("Создана категория с id {}", saved.getCategoryId());
        return saved;
    }

    public Category getCategoryById(Integer id) {
        if (id == null) {
            throw new BadRequestException("ID категории не может быть null");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена категория с id: " + id));
    }

    public List<Category> getAllCategories(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    public Category updateCategory(Integer id, @Valid Category updatedCategory) {
        if (id == null) {
            throw new BadRequestException("ID категории не может быть null");
        }
        validateCategory(updatedCategory);
        Category existingCategory = getCategoryById(id);

        if (!existingCategory.getCategoryName().equals(updatedCategory.getCategoryName())) {
            if (categoryRepository.existsByCategoryName(updatedCategory.getCategoryName())) {
                throw new BadRequestException("Категория должна быть уникальной");
            }
        }

        existingCategory.setCategoryName(updatedCategory.getCategoryName());
        existingCategory.setDescription(updatedCategory.getDescription());
        Category saved = categoryRepository.save(existingCategory);
        logger.info("Обновлена категория с id {}", saved.getCategoryId());
        return saved;
    }

    public void deleteCategory(Integer id) {
        if (id == null) {
            throw new BadRequestException("ID категории не может быть null");
        }
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Не найдена категория с id: " + id);
        }
        categoryRepository.deleteById(id);
        logger.info("Удалена категория с id {}", id);
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new BadRequestException("Категория не может быть null");
        }
        String name = category.getCategoryName();
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Название категории не может быть пустым");
        }
        if (name.length() > 100) {
            throw new BadRequestException("Название категории не может быть длиннее 100 символов");
        }
    }
}
