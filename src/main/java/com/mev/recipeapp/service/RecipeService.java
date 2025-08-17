package com.mev.recipeapp.service;


import com.mev.recipeapp.dtos.RecipeSummaryDTO;
import com.mev.recipeapp.dtos.requests.CreateRecipeRequest;
import com.mev.recipeapp.dtos.RecipeDTO;
import com.mev.recipeapp.models.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    Recipe createRecipe(CreateRecipeRequest recipeRequest, String email);

    Recipe updateRecipe(Long id, CreateRecipeRequest recipeRequest);

    void deleteRecipeById(Long id);

    Recipe getRecipeById(Long id);

    Page<RecipeDTO> getAllRecipes(Long categoryId, String recipeName, Pageable pageable);

    List<RecipeDTO> getFrontPageRecipes();

    RecipeDTO getRecipeWithDetails(Long recipeId);

    List<RecipeSummaryDTO> getRecipeSummaryByUserId(Long userId);

    List<RecipeDTO> getRecipesByUserId(Long userId);
}
