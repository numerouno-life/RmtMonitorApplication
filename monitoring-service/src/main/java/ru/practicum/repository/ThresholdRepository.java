package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Threshold;

import java.util.Optional;

public interface ThresholdRepository extends JpaRepository<Threshold, Long> {
    Optional<Threshold> findByAggregateId(Long aggregateId);
}
