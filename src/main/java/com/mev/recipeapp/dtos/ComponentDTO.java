package com.mev.recipeapp.dtos;

import com.mev.recipeapp.models.Component;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentDTO {
    private Long id;
    private String componentName;
    private String imagePath;
    private String ingredients;
    private String instruction;

}

