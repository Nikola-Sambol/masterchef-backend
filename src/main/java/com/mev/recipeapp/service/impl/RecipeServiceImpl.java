package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.dtos.*;
import com.mev.recipeapp.dtos.requests.CreateRecipeRequest;
import com.mev.recipeapp.models.Category;
import com.mev.recipeapp.models.Component;
import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.models.User;
import com.mev.recipeapp.repository.CategoryRepository;
import com.mev.recipeapp.repository.ComponentRepository;
import com.mev.recipeapp.repository.RecipeRepository;
import com.mev.recipeapp.repository.UserRepository;
import com.mev.recipeapp.service.ImageService;
import com.mev.recipeapp.service.RecipeService;
import com.mev.recipeapp.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    private final ImageService imageService;

    private final VideoService videoService;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final ComponentRepository componentRepository;

    @Override
    public Recipe createRecipe(CreateRecipeRequest recipeRequest, String email) {
        User user = userRepository.findByEmailWithRole(email);

        if (user == null) {
            throw new RuntimeException("User with email: " + email + " does not exist!");
        }

        Category category = categoryRepository.findById(recipeRequest.getCategory())
                .orElseThrow(() -> new RuntimeException("Kategorija s id: " + recipeRequest.getCategory() + " ne postoji!"));

        Recipe recipe = new Recipe(recipeRequest.getName(),
                imageService.saveImage(recipeRequest.getImage()),
                videoService.saveVideo(recipeRequest.getVideo()),
                recipeRequest.getPreparationTime(),
                user,
                category);

        return recipeRepository.save(recipe);
    }


    @Override
    public Recipe updateRecipe(Long id, CreateRecipeRequest recipeRequest) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe with id: " + id + "does not exist!"));

        Category category = categoryRepository.findById(recipeRequest.getCategory())
                .orElseThrow(() -> new RuntimeException("Kategorija s id: " + recipeRequest.getCategory() + " ne postoji!"));

        if (recipeRequest.getName() != null && !recipeRequest.getName().isBlank()) {
            recipe.setRecipeName(recipeRequest.getName());
        }

        if (recipeRequest.getImage() != null && !recipeRequest.getImage().isEmpty()) {
            recipe.setImagePath(imageService.saveImageAndDeleteExisting(recipeRequest.getImage(), id));
        }

        if (recipeRequest.getVideo() != null) {
            if (recipe.getVideoPath() != null) {
                videoService.deleteVideo(id);
            }
            recipe.setVideoPath(videoService.saveVideo(recipeRequest.getVideo()));
        } else {
            if (recipe.getVideoPath() != null) {
                videoService.deleteVideo(id);
            }
            recipe.setVideoPath(null);
        }

        if (recipeRequest.getPreparationTime() != null && !recipeRequest.getPreparationTime().isBlank()) {
            recipe.setPreparationTime(recipeRequest.getPreparationTime());
        }

        if (recipeRequest.getCategory() != null) {
            recipe.setCategory(category);
        }

        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipeById(Long id) {
        imageService.deleteImage(id);
        videoService.deleteVideo(id);
        componentRepository.findByRecipeId(id).forEach(component -> imageService.deleteImageByPath(component.getImagePath()));
        recipeRepository.deleteById(id);
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recept s id: " + id + " ne postoji!"));
    }

    @Override
    public Page<RecipeDTO> getAllRecipes(Long categoryId, String recipeName, Pageable pageable) {
        return recipeRepository.findAllFiltered(categoryId, recipeName, pageable)
                .map(p -> new RecipeDTO(
                        p.getId(),
                        p.getRecipeName(),
                        p.getCreationDate() != null ? p.getCreationDate().toString() : null,
                        p.getImagePath(),
                        p.getVideoPath(),
                        p.getPreparationTime(),
                        null, // components null
                        null, // category null
                        new UserDTO(
                                p.getUserName(),
                                p.getUserSurname(),
                                p.getUserEmail()
                        )
                ));

    }

    @Override
    public List<RecipeDTO> getFrontPageRecipes() {
        return recipeRepository.findAllWithUsers()
                .stream()
                .map(recipe -> new RecipeDTO(
                        recipe.getId(),
                        recipe.getRecipeName(),
                        recipe.getCreationDate().toString(),
                        recipe.getImagePath(),
                        recipe.getVideoPath(),
                        recipe.getPreparationTime(),
                        null,
                        null,
                        new UserDTO(recipe.getUser().getName(), recipe.getUser().getSurname(), recipe.getUser().getEmail())
                ))
                .collect(Collectors.toList());

    }

    @Override
    public RecipeDTO getRecipeWithDetails(Long recipeId) {
        Recipe recipe = recipeRepository.findRecipeWithComponents(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));
        List<Component> components = recipe.getComponents();

        // Pretvaranje u DTO
        return new RecipeDTO(
                recipe.getId(),
                recipe.getRecipeName(),
                recipe.getCreationDate().toString(),
                recipe.getImagePath(),
                recipe.getVideoPath(),
                recipe.getPreparationTime(),
                components.stream().map(this::mapToComponentDTO).toList(),
                new CategoryDTO(recipe.getCategory().getName()),
                new UserDTO(recipe.getUser().getName(), recipe.getUser().getSurname(), recipe.getUser().getEmail())
        );
    }

    @Override
    public List<RecipeSummaryDTO> getRecipeSummaryByUserId(Long userId) {
        List<Recipe> recipes = recipeRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<RecipeSummaryDTO> recipeDTOS = new ArrayList<RecipeSummaryDTO>();

        for (Recipe recipe : recipes) {
            RecipeSummaryDTO newRecipe = new RecipeSummaryDTO(
                    recipe.getId(),
                    recipe.getRecipeName(),
                    recipe.getCreationDate().toString());

            recipeDTOS.add(newRecipe);
        }

        return recipeDTOS;

    }

    @Override
    public List<RecipeDTO> getRecipesByUserId(Long userId) {
        List<Recipe> recipes = recipeRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<RecipeDTO> recipeDTOS = new ArrayList<RecipeDTO>();

        for (Recipe recipe : recipes) {
            RecipeDTO newRecipe = new RecipeDTO(recipe.getId(),
                    recipe.getRecipeName(),
                    recipe.getCreationDate().toString(),
                    recipe.getImagePath(),
                    recipe.getVideoPath(),
                    recipe.getPreparationTime(),
                    null,
                    null,
                    new UserDTO(recipe.getUser().getName(), recipe.getUser().getSurname(), recipe.getUser().getEmail()));

            recipeDTOS.add(newRecipe);
        }

        return recipeDTOS;
    }

    private ComponentDTO mapToComponentDTO(Component component) {
        return new ComponentDTO(
                component.getId(),
                component.getComponentName(),
                component.getImagePath(),
                component.getIngredients() != null ? component.getIngredients().getDescription() : null,
                component.getInstruction().getDescription()
        );
    }
}
