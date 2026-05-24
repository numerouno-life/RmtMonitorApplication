package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.enums.AggregateType;
import ru.practicum.model.Aggregate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Важно: отключаем автоматическую замену
@Testcontainers
public class AggregateRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-schema.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "schema_control");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AggregateRepository aggregateRepository;

    @BeforeEach
    void setUp() {
        aggregateRepository.deleteAll();
        // Сброс последовательности для PostgreSQL
        entityManager.getEntityManager()
                .createNativeQuery("ALTER SEQUENCE schema_control.aggregates_aggregate_id_seq RESTART WITH 1")
                .executeUpdate();
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