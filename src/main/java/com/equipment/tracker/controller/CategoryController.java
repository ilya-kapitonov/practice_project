package com.equipment.tracker.controller;

import com.equipment.tracker.entity.Category;
import com.equipment.tracker.service.CategoryService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Создает новую категорию.
     * @param category Объект категории, полученный из тела запроса.
     * @return Созданная категория и статус 201 Created.
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Получает категорию по её ID.
     * @param id ID категории.
     * @return Найденная категория и статус 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Получает список всех категорий.
     * Поддерживает сортировку по имени категории.
     * @param sortBy Поле для сортировки (по умолчанию "categoryName").
     * @param sortDir Направление сортировки (ASC или DESC, по умолчанию "ASC").
     * @return Список категорий и статус 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(defaultValue = "categoryName") String sortBy, @RequestParam(defaultValue = "ASC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        List<Category> categories = categoryService.getAllCategories(sort);
        return ResponseEntity.ok(categories);
    }

    /**
     * Обновляет существующую категорию.
     * @param id ID категории, которую нужно обновить.
     * @param updatedCategory Объект категории с обновленными данными.
     * @return Обновленная категория и статус 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category updatedCategory) {
        Category category = categoryService.updateCategory(id, updatedCategory);
        return ResponseEntity.ok(category);
    }

    /**
     * Удаляет категорию по её ID.
     * @param id ID категории, которую нужно удалить.
     * @return Статус 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
