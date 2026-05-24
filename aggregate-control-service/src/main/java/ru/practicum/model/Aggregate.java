package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.AggregateState;
import ru.practicum.enums.AggregateType;
import ru.practicum.enums.ControlMode;

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

    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    AggregateType type;

    @Column(name = "has_temperature_sensors", nullable = false)
    Boolean hasTemperatureSensors; // Есть ли температурные датчики

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state")
    AggregateState currentState;

    @Enumerated(EnumType.STRING)
    @Column(name = "control_mode")
    private ControlMode controlMode;
}
