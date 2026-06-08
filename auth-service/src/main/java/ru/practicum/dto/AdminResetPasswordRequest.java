package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminResetPasswordRequest(
        @NotBlank(message = "Новый пароль обязателен")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String newPassword
) {
}
