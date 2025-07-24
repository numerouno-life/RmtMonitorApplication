package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.enums.AggregateType;
import ru.practicum.model.Aggregate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AggregateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AggregateRepository aggregateRepository;

    @BeforeEach
    void setUp() {
        aggregateRepository.deleteAll();
    }


    @Test
    void findByNameContainsIgnoreCase_ShouldBeCaseInsensitive() {
        Aggregate aggregate1 = Aggregate.builder()
                .name("Test Aggregate")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();
        Aggregate aggregate2 = Aggregate.builder()
                .name("another test")
                .type(AggregateType.VM_40)
                .hasTemperatureSensors(false)
                .build();

        entityManager.persist(aggregate1);
        entityManager.persist(aggregate2);
        entityManager.flush();

        // When
        List<Aggregate> found = aggregateRepository.findByNameContainsIgnoreCase("TEST");
        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Aggregate::getName)
                    .containsExactlyInAnyOrder("Test Aggregate", "another test");

    }

    @Test
    void findByType_ShouldReturnCorrectResults() {
        for (int i = 0; i < 5; i++) {
            Aggregate vd18 = Aggregate.builder()
                    .name("VD Aggregate " + i)
                    .type(AggregateType.VD_18)
                    .hasTemperatureSensors(true)
                    .build();
            entityManager.persist(vd18);
        }
        Aggregate vm40 = Aggregate.builder()
                .name("VM Aggregate")
                .type(AggregateType.VM_40)
                .hasTemperatureSensors(true)
                .build();
        entityManager.persist(vm40);
        entityManager.flush();

        // When
        List<Aggregate> found = aggregateRepository.findByType(AggregateType.VD_18);
        // Then
        assertThat(found).hasSize(5);
        assertThat(found.get(0).getType()).isEqualTo(AggregateType.VD_18);
        assertThat(found.get(0).getName()).isEqualTo("VD Aggregate 0");
        assertThat(found.get(1).getName()).isEqualTo("VD Aggregate 1");

    }

    @Test
    void existsByNameIgnoreCase_ShouldReturnTrueWhenExists() {
        Aggregate vm40 = Aggregate.builder()
                .name("VM Aggregate")
                .type(AggregateType.VM_40)
                .hasTemperatureSensors(true)
                .build();
        entityManager.persist(vm40);
        entityManager.flush();

        boolean exists = aggregateRepository.existsByNameIgnoreCase("vm aggregate");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_ShouldReturnFalseWhenNotExists() {
        Aggregate vm40 = Aggregate.builder()
                .name("VM Aggregate")
                .type(AggregateType.VM_40)
                .hasTemperatureSensors(true)
                .build();
        entityManager.persist(vm40);
        entityManager.flush();

        boolean exists = aggregateRepository.existsByNameIgnoreCase("Non existing aggregate");
        assertThat(exists).isFalse();
    }

    @Test
    void findAll_ShouldReturnAllAggregates() {
        Aggregate agg1 = Aggregate.builder()
                .name("Agg 1")
                .type(AggregateType.VM_40)
                .hasTemperatureSensors(true)
                .build();
        Aggregate agg2 = Aggregate.builder()
                .name("Agg 2")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();
        entityManager.persist(agg2);
        entityManager.persist(agg1);
        entityManager.flush();

        List<Aggregate> found = aggregateRepository.findAll();
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getType()).isEqualTo(AggregateType.VD_18);
        assertThat(found.get(0).getName()).isEqualTo("Agg 2");
        assertThat(found.get(1).getName()).isEqualTo("Agg 1");
    }

}
