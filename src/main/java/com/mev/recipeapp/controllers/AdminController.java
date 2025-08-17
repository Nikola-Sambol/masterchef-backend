package com.mev.recipeapp.controllers;

import com.mev.recipeapp.dtos.response.UserInfoResponse;
import com.mev.recipeapp.models.Category;
import com.mev.recipeapp.repository.UserRepository;
import com.mev.recipeapp.service.CategoryService;
import com.mev.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody String category) {

        Category newCategory = categoryService.createNewCategory(category);

        return ResponseEntity.ok(newCategory);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserInfoResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {

        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping("/suspend-user/{userId}")
    public void suspendUser(@PathVariable Long userId) {
        userService.suspendUser(userId);
    }

    // TODO - implement update category method
    // TODO - implement delete category method
}
