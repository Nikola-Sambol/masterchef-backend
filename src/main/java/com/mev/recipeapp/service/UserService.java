package com.mev.recipeapp.service;

import com.mev.recipeapp.dtos.requests.UserUpdateRequest;
import com.mev.recipeapp.dtos.response.LoginResponse;
import com.mev.recipeapp.dtos.response.UserInfoResponse;

import java.util.List;

public interface UserService {
    List<UserInfoResponse> getAllUsers();
    UserInfoResponse getUserById(Long userId);
    LoginResponse updateUser(Long userId, UserUpdateRequest userUpdaterequest);
    void suspendUser(Long userId);
    void deleteUserById(Long userId);
}
