package com.mev.recipeapp.dtos.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateComponentRequest {
    private String name;
    private String instructions;
    private MultipartFile image;
    private List<String> ingredients;
}
