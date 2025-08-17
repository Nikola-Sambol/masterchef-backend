package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.dtos.ComponentDTO;
import com.mev.recipeapp.dtos.requests.CreateComponentRequest;
import com.mev.recipeapp.models.Component;
import com.mev.recipeapp.models.Ingredients;
import com.mev.recipeapp.models.Instruction;
import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.repository.ComponentRepository;
import com.mev.recipeapp.service.ComponentService;
import com.mev.recipeapp.service.ImageService;
import com.mev.recipeapp.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComponentServiceImpl implements ComponentService {

    private final ComponentRepository componentRepository;
    private final RecipeService recipeService;
    private final ImageService imageService;

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
            dto.setImageKey(formMap.getFirst(prefix + "[imageKey]"));

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
                dto.setImageKey(imageService.saveImage(image));
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

            Component newComponent = new Component(
                    component.getName(),
                    component.getImageKey(),
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
                        component.getImagePath(),
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

            // Ako frontend pošalje flag deleteImage
            if ("true".equals(component.getImageKey())) {
                imageService.deleteImageByPath(updatedComponent.getImagePath());
                updatedComponent.setImagePath(null);
            }
// Ako frontend pošalje novu sliku (tj. path koji ti je spremio imageService)
            else if (component.getImageKey() != null && !component.getImageKey().isBlank()) {
                updatedComponent.setImagePath(component.getImageKey());
            }
// Inače -> ne mijenjaj imagePath (ostaje stara slika)

            if (Objects.equals(component.getImageKey(), "true")) {
                imageService.deleteImageByPath(updatedComponent.getImagePath());
            }

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


            if (component.getImageKey() != null) {
                updatedComponent.setImagePath(component.getImageKey());
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

                Component componentEntity = new Component(
                        newComp.getName(),
                        newComp.getImageKey(),
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
