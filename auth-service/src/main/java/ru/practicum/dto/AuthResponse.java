package ru.practicum.dto;

public record AuthResponse(
        String token,
        String type,
        Long userId,
        String personnelNumber,
        String role
) {
    public AuthResponse(String token, Long userId, String personnelNumber, String role) {
        this(token, "Bearer", userId, personnelNumber, role);
    }
}