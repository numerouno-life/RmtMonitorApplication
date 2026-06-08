package ru.practicum.dto;

import jakarta.validation.constraints.Email;
import ru.practicum.model.User;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        @Email(message = "Некорректный формат email")
        String email,
        User.UserStatus status
) {
}
