package com.mev.recipeapp.service.impl;

import com.mev.recipeapp.dtos.requests.UserUpdateRequest;
import com.mev.recipeapp.dtos.response.LoginResponse;
import com.mev.recipeapp.dtos.response.UserInfoResponse;
import com.mev.recipeapp.models.CustomUserDetails;
import com.mev.recipeapp.models.User;
import com.mev.recipeapp.repository.UserRepository;
import com.mev.recipeapp.security.jwt.JwtUtil;
import com.mev.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public List<UserInfoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserInfoResponse(user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        List.of(user.getRole().getRoleName().name()),
                        user.isEnabled(),
                        user.getCreatedDate().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public UserInfoResponse getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return new UserInfoResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                List.of(user.getRole().getRoleName().name()),
                user.isEnabled(),
                user.getCreatedDate().toString()
        );
    }

    @Override
    public LoginResponse updateUser(Long userId, UserUpdateRequest userUpdaterequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (userUpdaterequest.getName() != null && !userUpdaterequest.getName().isBlank()) {
            user.setName(userUpdaterequest.getName());
        }
        if (userUpdaterequest.getSurname() != null && !userUpdaterequest.getSurname().isBlank()) {
            user.setSurname(userUpdaterequest.getSurname());
        }

        if (userUpdaterequest.getEmail() != null && !userUpdaterequest.getEmail().isBlank()) {
            user.setEmail(userUpdaterequest.getEmail());
            userRepository.save(user);
            return new LoginResponse(jwtUtil.generateTokenFromUsername(new CustomUserDetails(user)),
                    userUpdaterequest.getEmail());
        }

        userRepository.save(user);
        return null;
    }

    @Override
    public void suspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setEnabled(false);

        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}
