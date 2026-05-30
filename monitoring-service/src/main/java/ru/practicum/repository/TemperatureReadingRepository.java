package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.TemperatureReading;

import java.time.LocalDateTime;
import java.util.List;

public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {

    List<TemperatureReading> findByAggregateId(Long aggregateId);

    // Поиск за период
    List<TemperatureReading> findByAggregateIdAndReadingDateBetween(Long aggreagate_id,
                                                                    LocalDateTime start, LocalDateTime end);

    // Последние N записей для агрегата
    @Query("""
            SELECT tr FROM TemperatureReading AS tr
            WHERE tr.aggregateId = :aggregateId
            ORDER BY tr.readingDate DESC LIMIT :limit
            """)
    List<TemperatureReading> findLatestByAggregateId(@Param("aggregateId") Long aggregateId,
                                                     @Param("limit") int limit);
}
