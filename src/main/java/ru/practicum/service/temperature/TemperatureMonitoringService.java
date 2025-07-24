package ru.practicum.service.temperature;

public interface TemperatureMonitoringService {

    void startMonitoring(Long aggregateId);

    void stopMonitoring(Long aggregateId);

    boolean isMonitoring(Long aggregateId);
}
