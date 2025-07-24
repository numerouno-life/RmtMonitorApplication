package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.AggregateDTO;
import ru.practicum.model.Aggregate;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AggregateMapper {

    Aggregate toEntity(AggregateDTO dto);

    AggregateDTO toDto(Aggregate entity);
}
