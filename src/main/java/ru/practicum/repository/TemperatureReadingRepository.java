package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.TemperatureReading;

public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {
}
