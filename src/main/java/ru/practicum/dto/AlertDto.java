package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDto {
    String message;
    AlertLevel level;
    Long aggregateId;
    Double frontTemperature;
    Double rearTemperature;

    public enum AlertLevel {
        WARNING, ALARM
    }
}
