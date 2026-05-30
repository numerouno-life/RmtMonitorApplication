package ru.practicum.dto;

import lombok.Builder;

@Builder
public record AlertDto(
        String message,
        AlertLevel level,
        Long aggregateId,
        Double frontTemperature,
        Double rearTemperature
) {
    public enum AlertLevel {
        WARNING, ALARM
    }
}