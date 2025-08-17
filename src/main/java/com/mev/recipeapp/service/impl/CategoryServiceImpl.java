package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.dtos.CategoryDTO;
import com.mev.recipeapp.models.Category;
import com.mev.recipeapp.repository.CategoryRepository;
import com.mev.recipeapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Category createNewCategory(String categoryName) {

        Category category = new Category(categoryName);

        return categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Long categoryId, String categoryName) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        category.setName(categoryName);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
