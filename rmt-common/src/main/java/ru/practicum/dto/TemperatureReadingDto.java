package ru.practicum.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TemperatureReadingDto(
        Long id,
        LocalDateTime readingDate,
        Double frontBearingTemp,
        Double rearBearingTemp,
        Boolean isWarningTriggered,
        Boolean isAlarmTriggered,
        Long aggregateId
) {
}
