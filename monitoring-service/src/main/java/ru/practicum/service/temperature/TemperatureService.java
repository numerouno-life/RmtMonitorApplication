package ru.practicum.service.temperature;

import ru.practicum.dto.TemperatureReadingDto;

import java.util.List;

public interface TemperatureService {

    void readAndStoreTemperatures(Long aggregateId);

    List<TemperatureReadingDto> getLatestReadings();

    List<TemperatureReadingDto> getReadingForAggregate(Long aggregateId);
}
