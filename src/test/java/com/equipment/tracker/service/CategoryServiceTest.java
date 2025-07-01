package com.equipment.tracker.service;
import com.equipment.tracker.entity.Category;
import com.equipment.tracker.exception.BadRequestException;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_Success() {
        Category category = new Category();
        category.setCategoryName("Спорт");
        category.setDescription("Описание");

        when(categoryRepository.existsByCategoryName("Спорт")).thenReturn(false);
        when(categoryRepository.save(category)).thenAnswer(i -> {
            Category c = i.getArgument(0);
            c.setCategoryId(1);
            return c;
        });

        Category created = categoryService.createCategory(category);

        assertNotNull(created);
        assertEquals(1, created.getCategoryId());
        assertEquals("Спорт", created.getCategoryName());
        verify(categoryRepository).save(category);
    }

    @Test
    void createCategory_DuplicateName_ThrowsBadRequest() {
        Category category = new Category();
        category.setCategoryName("Спорт");

        when(categoryRepository.existsByCategoryName("Спорт")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> categoryService.createCategory(category));
        assertEquals("Категория должна быть уникальной", ex.getMessage());
    }

    @Test
    void getCategoryById_Found() {
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Спорт");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        Category found = categoryService.getCategoryById(1);

        assertEquals("Спорт", found.getCategoryName());
    }

    @Test
    void getCategoryById_NotFound_ThrowsNotFound() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> categoryService.getCategoryById(1));
        assertTrue(ex.getMessage().contains("Не найдена категория"));
    }

    @Test
    void updateCategory_Success() {
        Category existing = new Category();
        existing.setCategoryId(1);
        existing.setCategoryName("Спорт");

        Category updated = new Category();
        updated.setCategoryName("Новый спорт");
        updated.setDescription("Новое описание");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByCategoryName("Новый спорт")).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Category result = categoryService.updateCategory(1, updated);

        assertEquals("Новый спорт", result.getCategoryName());
        assertEquals("Новое описание", result.getDescription());
    }

    @Test
    void updateCategory_DuplicateName_ThrowsBadRequest() {
        Category existing = new Category();
        existing.setCategoryId(1);
        existing.setCategoryName("Спорт");

        Category updated = new Category();
        updated.setCategoryName("Другой спорт");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByCategoryName("Другой спорт")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> categoryService.updateCategory(1, updated));
        assertEquals("Категория должна быть уникальной", ex.getMessage());
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1));
        verify(categoryRepository).deleteById(1);
    }

    @Test
    void deleteCategory_NotFound_ThrowsNotFound() {
        when(categoryRepository.existsById(1)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> categoryService.deleteCategory(1));
        assertTrue(ex.getMessage().contains("Не найдена категория"));
    }
}
