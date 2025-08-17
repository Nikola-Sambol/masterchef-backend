package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.dtos.ComponentDTO;
import com.mev.recipeapp.dtos.requests.CreateComponentRequest;
import com.mev.recipeapp.models.Component;
import com.mev.recipeapp.models.Ingredients;
import com.mev.recipeapp.models.Instruction;
import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.repository.ComponentRepository;
import com.mev.recipeapp.service.ComponentService;
import com.mev.recipeapp.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComponentServiceImpl implements ComponentService {

    private final ComponentRepository componentRepository;
    private final RecipeService recipeService;

    @Override
    public List<CreateComponentRequest> parseComponents(
            MultiValueMap<String, String> formMap,
            Map<String, MultipartFile> fileMap
    ) {
        List<CreateComponentRequest> componentList = new ArrayList<>();

        int index = 0;
        while (true) {
            String prefix = "components[" + index + "]";
            String nameKey = prefix + "[name]";
            if (!formMap.containsKey(nameKey)) break;

            CreateComponentRequest dto = new CreateComponentRequest();
            dto.setName(formMap.getFirst(nameKey));
            dto.setInstructions(formMap.getFirst(prefix + "[instructions]"));

            // Parsiraj sastojke
            List<String> ingredients = new ArrayList<>();
            int ingIndex = 0;
            while (true) {
                String ingKey = prefix + "[ingredients][" + ingIndex + "]";
                if (!formMap.containsKey(ingKey)) break;
                ingredients.add(formMap.getFirst(ingKey));
                ingIndex++;
            }
            dto.setIngredients(ingredients);

            // Upari sliku
            MultipartFile image = fileMap.get(prefix + "[image]");
            if (image != null && !image.isEmpty()) {
                dto.setImage(image); // spremi file direktno
            }

            componentList.add(dto);
            index++;
        }

        return componentList;
    }

    @Override
    public void saveComponents(Long recipeId, List<CreateComponentRequest> components) {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        for (CreateComponentRequest component : components) {
            Instruction instruction = new Instruction(component.getInstructions());

            List<String> filteredIngredients = component.getIngredients().stream()
                    .filter(ing -> ing != null && !ing.trim().isEmpty())
                    .collect(Collectors.toList());

            String ingredientsAsString = String.join(", ", filteredIngredients);
            Ingredients ingredients = new Ingredients(ingredientsAsString);

            byte[] imageBytes = null;
            try {
                if (component.getImage() != null && !component.getImage().isEmpty()) {
                    imageBytes = component.getImage().getBytes();
                }
            } catch (Exception e) {
                throw new RuntimeException("Greška pri spremanju slike komponente!", e);
            }

            Component newComponent = new Component(
                    component.getName(),
                    imageBytes,
                    recipe,
                    ingredients,
                    instruction
            );

            componentRepository.save(newComponent);
        }
    }

    @Override
    public List<ComponentDTO> getComponentsForRecipe(Long recipeId) {
        return componentRepository.findByRecipeId(recipeId)
                .stream()
                .map(component -> new ComponentDTO(
                        component.getId(),
                        component.getComponentName(),
                        component.getImage() != null ? Base64.getEncoder().encodeToString(component.getImage()) : null,
                        component.getIngredients().getDescription(),
                        component.getInstruction().getDescription()
                ))
                .toList();
    }

    @Override
    public void updateComponents(Long recipeId, List<CreateComponentRequest> components) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        List<Component> existingComponents = componentRepository.findByRecipeId(recipeId);

        int existingCount = existingComponents.size();
        int incomingCount = components.size();

        // 1. Ažuriraj postojeće komponente
        for (int i = 0; i < Math.min(existingCount, incomingCount); i++) {
            CreateComponentRequest component = components.get(i);
            Component updatedComponent = existingComponents.get(i);

            if (component.getName() != null && !component.getName().isBlank()) {
                updatedComponent.setComponentName(component.getName());
            }

            List<String> filteredIngredients = component.getIngredients().stream()
                    .filter(ing -> ing != null && !ing.trim().isEmpty())
                    .collect(Collectors.toList());
            String ingredientsAsString = String.join(", ", filteredIngredients);
            if (!ingredientsAsString.isBlank()) {
                updatedComponent.getIngredients().setDescription(ingredientsAsString);
            }

            if (component.getInstructions() != null && !component.getInstructions().isBlank()) {
                updatedComponent.getInstruction().setDescription(component.getInstructions());
            }

            try {
                if (component.getImage() != null && !component.getImage().isEmpty()) {
                    updatedComponent.setImage(component.getImage().getBytes());
                }
            } catch (Exception e) {
                throw new RuntimeException("Greška pri ažuriranju slike komponente!", e);
            }

            componentRepository.save(updatedComponent);
        }

        // 2. Dodaj nove komponente ako ih ima više nego postojećih
        if (incomingCount > existingCount) {
            for (int i = existingCount; i < incomingCount; i++) {
                CreateComponentRequest newComp = components.get(i);

                List<String> filteredIngredients = newComp.getIngredients().stream()
                        .filter(ing -> ing != null && !ing.trim().isEmpty())
                        .collect(Collectors.toList());
                String ingredientsAsString = String.join(", ", filteredIngredients);

                byte[] imageBytes = null;
                try {
                    if (newComp.getImage() != null && !newComp.getImage().isEmpty()) {
                        imageBytes = newComp.getImage().getBytes();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Greška pri spremanju slike nove komponente!", e);
                }

                Component componentEntity = new Component(
                        newComp.getName(),
                        imageBytes,
                        recipe,
                        new Ingredients(ingredientsAsString),
                        new Instruction(newComp.getInstructions())
                );

                componentRepository.save(componentEntity);
            }
        }
    }

    @Override
    public void deleteComponent(Long componentId) {
        componentRepository.deleteById(componentId);
    }
}
