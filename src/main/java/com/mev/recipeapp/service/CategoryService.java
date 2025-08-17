package com.mev.recipeapp.service;

import com.mev.recipeapp.dtos.CategoryDTO;
import com.mev.recipeapp.models.Category;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    Category createNewCategory(String categoryName);

    void updateCategory(Long categoryId, String categoryName);

    void deleteCategoryById(Long categoryId);
}
