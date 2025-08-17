package com.mev.recipeapp.controllers;

import com.mev.recipeapp.dtos.RecipeSummaryDTO;
import com.mev.recipeapp.dtos.requests.CreateRecipeRequest;
import com.mev.recipeapp.dtos.RecipeDTO;
import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.models.User;
import com.mev.recipeapp.repository.UserRepository;
import com.mev.recipeapp.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    private final UserRepository userRepository;

    @GetMapping("/public/{id}")
    public ResponseEntity<RecipeDTO> getRecipeWithDetails(@PathVariable Long id) {

        RecipeDTO recipeDTO = recipeService.getRecipeWithDetails(id);
        return ResponseEntity.ok(recipeDTO);
    }

    @GetMapping("/public")
    public Page<RecipeDTO> getAllRecipes(@RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) String recipeName,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return recipeService.getAllRecipes(categoryId, recipeName, PageRequest.of(page, size));
    }

    @GetMapping("/public/frontpage")
    public List<RecipeDTO> getFrontPageRecipes() {
        return recipeService.getFrontPageRecipes();
    }

    @PostMapping
    public ResponseEntity<Long> createRecipe(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute CreateRecipeRequest recipeRequest) {

        Recipe recipe = recipeService.createRecipe(recipeRequest, userDetails.getUsername());
        return ResponseEntity.ok(recipe.getId());
    }

    @PostMapping("/update/{id}")
    public ResponseEntity updateRecipe(@PathVariable Long id,
                                       @ModelAttribute CreateRecipeRequest recipeRequest) {

        recipeService.updateRecipe(id, recipeRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping({"", "/{userId}"})
    public ResponseEntity<List<RecipeSummaryDTO>> getRecipeSummaryByUserId(@PathVariable(required = false) Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userId == null) {
            User user = userRepository.findByEmailWithRole(userDetails.getUsername());
            List<RecipeSummaryDTO> recipeDTOS = recipeService.getRecipeSummaryByUserId(user.getId());
            return ResponseEntity.ok(recipeDTOS);
        } else {
            List<RecipeSummaryDTO> recipeDTOS = recipeService.getRecipeSummaryByUserId(userId);

            if (recipeDTOS.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(recipeDTOS);
        }
    }

    @GetMapping("user")
    public ResponseEntity<List<RecipeDTO>> getRecipesByUserId(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmailWithRole(userDetails.getUsername());

        List<RecipeDTO> recipeDTOS = recipeService.getRecipesByUserId(user.getId());
        return ResponseEntity.ok(recipeDTOS);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecipeById(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);
    }
}
