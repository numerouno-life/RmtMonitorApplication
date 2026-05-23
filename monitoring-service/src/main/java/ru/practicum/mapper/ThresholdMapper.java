package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.ThresholdDto;
import ru.practicum.model.Threshold;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThresholdMapper {

    @Mapping(target = "aggregateId", source = "aggregateId")
    Threshold toEntity(ThresholdDto dto);

    @Mapping(target = "aggregateId", source = "aggregateId")
    ThresholdDto toDto(Threshold entity);
}
