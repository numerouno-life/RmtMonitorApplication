package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "thresholds")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Threshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "threshold_id")
    Long id;

    @Column(name = "aggregate_id", nullable = false)
    Long aggregateId;

    @Column(name = "warning_threshold")
    Double warningThreshold;

    @Column(name = "warning_timestamp", nullable = false)
    LocalDateTime warningTimestamp;

    @Column(name = "alarm_threshold")
    Double alarmThreshold;

    @Column(name = "alarm_timestamp", nullable = false)
    LocalDateTime alarmTimestamp;
}
