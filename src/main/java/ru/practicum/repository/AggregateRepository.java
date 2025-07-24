package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.AggregateType;
import ru.practicum.model.Aggregate;

import java.util.List;

public interface AggregateRepository extends JpaRepository<Aggregate, Long> {

    List<Aggregate> findByNameContainsIgnoreCase(String name);

    List<Aggregate> findByType(AggregateType type);

    boolean existsByNameIgnoreCase(String name);
}
