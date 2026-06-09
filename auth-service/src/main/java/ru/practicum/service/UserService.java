package ru.practicum.service;

import ru.practicum.dto.UserRegistrationRequest;
import ru.practicum.dto.UserResponse;
import ru.practicum.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    UserResponse getUserProfileById(Long userId);

    UserResponse updateUserProfile(Long userId, UserUpdateRequest request);

    void deleteUserById(Long userId);

    List<UserResponse> getAllActiveMachinists();

}
