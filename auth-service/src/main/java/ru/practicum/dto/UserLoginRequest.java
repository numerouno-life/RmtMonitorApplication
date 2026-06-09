package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank(message = "Фамилия не может быть пустой")
        String lastName,
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}
