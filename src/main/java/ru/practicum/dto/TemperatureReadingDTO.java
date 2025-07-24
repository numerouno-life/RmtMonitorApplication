package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureReadingDTO {
    Long id;
    LocalDateTime readingDate;
    Double frontBearingTemp;
    Double rearBearingTemp;
    Boolean isWarningTriggered;
    Boolean isAlarmTriggered;
    Long aggregateId;
}
