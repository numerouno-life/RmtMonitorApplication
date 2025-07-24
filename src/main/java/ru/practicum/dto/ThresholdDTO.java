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
public class ThresholdDTO {
    Long id;
    Long aggregateId;
    Double warningThreshold;
    Double alarmThreshold;
    LocalDateTime warningTimestamp;
    LocalDateTime alarmTimestamp;
}
