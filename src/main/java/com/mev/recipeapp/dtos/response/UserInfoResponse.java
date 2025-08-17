package com.mev.recipeapp.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<String> role;
    private boolean enabled;
    private String creationDate;
}
