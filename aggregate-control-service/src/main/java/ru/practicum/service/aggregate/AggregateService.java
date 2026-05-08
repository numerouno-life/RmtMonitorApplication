package ru.practicum.service.aggregate;

import ru.practicum.dto.AggregateDTO;

import java.util.List;

public interface AggregateService {

    AggregateDTO createAggregate(AggregateDTO aggregateDTO);

    void deleteAggregateById(Long id);

    List<AggregateDTO> getAllAggregates();

    AggregateDTO getAggregateById(Long id);

    AggregateDTO updateAggregate(Long aggregateId, AggregateDTO aggregateDTO);

    List<AggregateDTO> findByName(String name);

    List<AggregateDTO> findByType(String type);

}
