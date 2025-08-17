package com.mev.recipeapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String categoryName;

    public CategoryDTO(String categoryName) {
        this.categoryName = categoryName;
    }
}
