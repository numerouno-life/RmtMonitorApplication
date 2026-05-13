package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.ThresholdDto;
import ru.practicum.model.Threshold;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThresholdMapper {

    @Mapping(source = "aggregateId", target = "aggregateDto.id")
    Threshold toEntity(ThresholdDto dto);

    @Mapping(source = "aggregate.id", target = "aggregateId")
    ThresholdDto toDto(Threshold entity);
}
