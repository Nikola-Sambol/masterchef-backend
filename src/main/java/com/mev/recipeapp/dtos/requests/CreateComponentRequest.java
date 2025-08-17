package com.mev.recipeapp.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateComponentRequest {
    private String name;
    private String instructions;
    private String imageKey;
    private List<String> ingredients;
}
