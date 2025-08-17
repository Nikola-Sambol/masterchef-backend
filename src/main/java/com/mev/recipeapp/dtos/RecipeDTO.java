package com.mev.recipeapp.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String recipeName;
    private String creationDate;
    private String imagePath;
    private String videoPath;
    private String preparationTime;
    private List<ComponentDTO> components;
    private CategoryDTO category;
    private UserDTO user;
}
