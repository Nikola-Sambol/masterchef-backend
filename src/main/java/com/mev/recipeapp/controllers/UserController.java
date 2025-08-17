package com.mev.recipeapp.controllers;

import com.mev.recipeapp.dtos.requests.UserUpdateRequest;
import com.mev.recipeapp.dtos.response.LoginResponse;
import com.mev.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        LoginResponse loginResponse = userService.updateUser(id, userUpdateRequest);
        if (loginResponse != null) {
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
