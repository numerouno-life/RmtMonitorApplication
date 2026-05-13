package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.TemperatureReadingDto;
import ru.practicum.model.TemperatureReading;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TemperatureReadingMapper {

    @Mapping(source = "aggregateId", target = "aggregate.id")
    TemperatureReading toEntity(TemperatureReadingDto dto);

    @Mapping(source = "aggregate.id", target = "aggregateId")
    TemperatureReadingDto toDto(TemperatureReading entity);
}
