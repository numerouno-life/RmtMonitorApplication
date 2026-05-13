package ru.practicum.dto;

import lombok.Builder;
import ru.practicum.enums.AggregateType;

@Builder
public record AggregateDto(
        Long id,
        String name,
        AggregateType type,
        Boolean hasTemperatureSensors  // Нужно для проверки в мониторинге
) {
}
