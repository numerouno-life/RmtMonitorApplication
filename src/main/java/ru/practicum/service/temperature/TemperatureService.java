package ru.practicum.service.temperature;

import ru.practicum.dto.TemperatureReadingDTO;

import java.util.List;

public interface TemperatureService {

    void readAndStoreTemperatures(Long aggregateId);

    List<TemperatureReadingDTO> getLatestReadings();

    List<TemperatureReadingDTO> getReadingForAggregate(Long aggregateId);
}
