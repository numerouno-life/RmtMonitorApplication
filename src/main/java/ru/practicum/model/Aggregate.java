package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.AggregateType;

@Entity
@Table(name = "aggregates")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Aggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aggregate_id")
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    AggregateType type;

    @Column(name = "has_temperature_sensors", nullable = false)
    Boolean hasTemperatureSensors; // Есть ли температурные датчики
}
