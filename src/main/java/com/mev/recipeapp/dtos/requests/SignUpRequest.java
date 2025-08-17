package com.mev.recipeapp.dtos.requests;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String surname;
    private String password;
    private String email;
}
