package com.equipment.tracker.controller;
import com.equipment.tracker.entity.Category;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_Success() throws Exception {
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Спорт");

        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Спорт"));
    }

    @Test
    void getCategoryById_Success() throws Exception {
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Спорт");

        when(categoryService.getCategoryById(1)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Спорт"));
    }

    @Test
    void getCategoryById_NotFound() throws Exception {
        when(categoryService.getCategoryById(1)).thenThrow(new NotFoundException("Не найдена категория"));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategories_Success() throws Exception {
        Category c1 = new Category();
        c1.setCategoryId(1);
        c1.setCategoryName("Спорт");
        Category c2 = new Category();
        c2.setCategoryId(2);
        c2.setCategoryName("Туризм");

        when(categoryService.getAllCategories(any(Sort.class))).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateCategory_Success() throws Exception {
        Category updated = new Category();
        updated.setCategoryId(1);
        updated.setCategoryName("Новый спорт");

        when(categoryService.updateCategory(eq(1), any(Category.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Новый спорт"));
    }

    @Test
    void deleteCategory_Success() throws Exception {
        doNothing().when(categoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
