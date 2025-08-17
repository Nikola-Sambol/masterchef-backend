package com.mev.recipeapp.controllers;

import com.mev.recipeapp.dtos.CategoryDTO;
import com.mev.recipeapp.dtos.requests.CategoryRequest;
import com.mev.recipeapp.models.Category;
import com.mev.recipeapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/public")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {

        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public void createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createNewCategory(categoryRequest.getCategoryName());
    }

    @PostMapping("/update/{editCategoryId}")
    public void updateCategory(@PathVariable Long editCategoryId, @RequestBody CategoryRequest categoryRequest) {
        categoryService.updateCategory(editCategoryId, categoryRequest.getCategoryName());
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategoryById(@PathVariable Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
    }
}
