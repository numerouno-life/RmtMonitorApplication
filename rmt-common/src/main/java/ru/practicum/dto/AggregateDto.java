package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.enums.AggregateType;

@Builder
public record AggregateDto(
        Long id,
        @NotBlank(message = "Name cannot be blank") String name,
        @NotNull(message = "Type cannot be null") AggregateType type,
        Boolean hasTemperatureSensors  // Нужно для проверки в мониторинге
) {
}
