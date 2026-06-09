package ru.practicum.dto;

import jakarta.validation.constraints.*;
import ru.practicum.model.User;

public record UserRegistrationRequest(
        @NotBlank(message = "Имя не может быть пустым")
        String firstName,
        @NotBlank(message = "Фамилия не может быть пустой")
        String lastName,
        @NotBlank(message = "Табельный номер не должен быть пуст")
        @Pattern(regexp = "^\\d+$", message = "Табельный номер должен содержать только цифры")
        String personnelNumber,
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,
        @Email(message = "Некорректный формат email")
        String email,
        @NotNull(message = "Роль должна быть указана")
        User.UserRole role
) {
}
