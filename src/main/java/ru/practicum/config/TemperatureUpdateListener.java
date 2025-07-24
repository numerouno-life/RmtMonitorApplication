package ru.practicum.config;

public interface TemperatureUpdateListener {
    void onTemperatureUpdate(Long aggregateId, Double frontTemp, Double rearTemp);
}