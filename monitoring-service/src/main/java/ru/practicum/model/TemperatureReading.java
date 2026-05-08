package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "temperature_readings")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class TemperatureReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temperature_reading_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "aggregate_id")
    Aggregate aggregate;

    @Column(name = "reading_date", nullable = false)
    @CreationTimestamp
    LocalDateTime readingDate;

    @Column(name = "front_bearing_temp")
    Double frontBearingTemp;

    @Column(name = "rear_bearing_temp")
    Double rearBearingTemp;

    @Column(name = "warning_triggered", nullable = false)
    Boolean isWarningTriggered;

    @Column(name = "alarm_triggered", nullable = false)
    Boolean isAlarmTriggered;
}
