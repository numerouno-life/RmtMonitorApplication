package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.AggregateDto;
import ru.practicum.model.Aggregate;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AggregateMapper {

    Aggregate toEntity(AggregateDto dto);

    AggregateDto toDto(Aggregate entity);
}
