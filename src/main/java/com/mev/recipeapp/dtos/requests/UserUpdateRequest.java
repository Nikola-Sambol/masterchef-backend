package com.mev.recipeapp.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String surname;
    private String email;
}
