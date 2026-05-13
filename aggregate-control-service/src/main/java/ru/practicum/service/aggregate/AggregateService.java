package ru.practicum.service.aggregate;

import ru.practicum.dto.AggregateDto;

import java.util.List;

public interface AggregateService {

    AggregateDto createAggregate(AggregateDto aggregateDTO);

    void deleteAggregateById(Long id);

    List<AggregateDto> getAllAggregates();

    AggregateDto getAggregateById(Long id);

    AggregateDto updateAggregate(Long aggregateId, AggregateDto aggregateDTO);

    List<AggregateDto> findByName(String name);

    List<AggregateDto> findByType(String type);

}
