package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.AggregateType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AggregateDTO {
    Long id;
    @NotBlank
    String name;
    @NotNull
    AggregateType type;
    @NotNull
    Boolean hasTemperatureSensors;
}
