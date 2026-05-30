package ru.practicum.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ThresholdDto(
        Long id,
        Long aggregateId,
        Double warningThreshold,
        Double alarmThreshold,
        LocalDateTime warningTimestamp,
        LocalDateTime alarmTimestamp
) {
}
