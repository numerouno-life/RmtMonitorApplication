package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserRegistrationRequest;
import ru.practicum.dto.UserResponse;
import ru.practicum.dto.UserUpdateRequest;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {
        return null;
    }

    @Override
    public UserResponse getUserProfileById(Long userId) {
        return null;
    }

    @Override
    public UserResponse updateUserProfile(Long userId, UserUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteUserById(Long userId) {

    }

    @Override
    public List<UserResponse> getAllActiveMachinists() {
        return List.of();
    }
}
