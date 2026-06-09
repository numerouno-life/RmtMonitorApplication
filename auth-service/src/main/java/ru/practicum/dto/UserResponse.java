package ru.practicum.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String personnelNumber,
        String firstName,
        String lastName,
        String email,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
