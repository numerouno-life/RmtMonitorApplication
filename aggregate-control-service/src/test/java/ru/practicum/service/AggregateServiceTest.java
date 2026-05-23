package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.dto.AggregateDto;
import ru.practicum.enums.AggregateType;
import ru.practicum.exception.custom.DuplicateAggregateException;
import ru.practicum.exception.custom.EntityNotFoundException;
import ru.practicum.exception.custom.ResponseStatusException;
import ru.practicum.mapper.AggregateMapper;
import ru.practicum.model.Aggregate;
import ru.practicum.repository.AggregateRepository;
import ru.practicum.service.aggregate.AggregateServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AggregateServiceTest {

    @InjectMocks
    private AggregateServiceImpl aggregateService;

    @Mock
    private AggregateRepository aggregateRepository;

    @Mock
    private AggregateMapper aggregateMapper;

    @Test
    void createAggregate_WhenValidData_ShouldSaveAndReturnDto() {
        AggregateDto inputDto = new AggregateDto(null, "Test Aggregate", AggregateType.VD_18, true);
        Aggregate entity = Aggregate.builder()
                .id(1L)
                .name("Test Aggregate")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();
        AggregateDto expectedDto = new AggregateDto(1L, "Test Aggregate", AggregateType.VD_18, true);

        when(aggregateRepository.existsByNameIgnoreCase("Test Aggregate")).thenReturn(false);
        when(aggregateMapper.toEntity(inputDto)).thenReturn(entity);
        when(aggregateRepository.save(entity)).thenReturn(entity);
        when(aggregateMapper.toDto(entity)).thenReturn(expectedDto);

        AggregateDto result = aggregateService.createAggregate(inputDto);

        assertThat(result.name()).isEqualTo("Test Aggregate");
        assertThat(result.id()).isEqualTo(1L);

        verify(aggregateRepository).existsByNameIgnoreCase("Test Aggregate");
        verify(aggregateRepository).save(entity);
    }

    @Test
    void createAggregate_WhenNameExists_ShouldThrowDuplicateException() {
        AggregateDto inputDto = new AggregateDto(null, "Duplicate Name", AggregateType.VD_18, true);

        when(aggregateRepository.existsByNameIgnoreCase("Duplicate Name")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> aggregateService.createAggregate(inputDto))
                .isInstanceOf(DuplicateAggregateException.class)
                .hasMessage("Агрегат с именем " + inputDto.name() + " уже существует");

        verify(aggregateRepository).existsByNameIgnoreCase("Duplicate Name");
        verify(aggregateRepository, never()).save(any());
    }

    @Test
    void getAggregateById_WhenExists_ShouldReturnDto() {
        Aggregate entity = Aggregate.builder()
                .id(1L)
                .name("Test Aggregate")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();

        AggregateDto expectedDto = new AggregateDto(1L, "Test Aggregate", AggregateType.VD_18, true);

        when(aggregateRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(aggregateMapper.toDto(entity)).thenReturn(expectedDto);

        //when
        AggregateDto result = aggregateService.getAggregateById(1L);

        //then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Aggregate");
        assertThat(result.type()).isEqualTo(AggregateType.VD_18);
    }

    @Test
    void getAggregateById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        Long id = 999L;
        when(aggregateRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> aggregateService.getAggregateById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Агрегат с id " + id + " не найден");
    }

    @Test
    void deleteAggregateById_WhenExists_ShouldDelete() {
        Long id = 1L;
        when(aggregateRepository.existsById(id)).thenReturn(true);

        aggregateService.deleteAggregateById(id);

        verify(aggregateRepository).existsById(id);
        verify(aggregateRepository).deleteById(id);
    }

    @Test
    void deleteAggregateById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        Long id = 999L;
        when(aggregateRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> aggregateService.deleteAggregateById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Агрегат с id " + id + " не найден");
    }

    @Test
    void updateAggregate_WhenValidData_ShouldUpdateAndReturnDto() {
        Long id = 1L;
        AggregateDto updateDto = new AggregateDto(id, "Updated Name", AggregateType.VM_40, false);
        Aggregate existingEntity = Aggregate.builder()
                .id(id)
                .name("Old Name")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();
        Aggregate updatedEntity = Aggregate.builder()
                .id(id)
                .name("Updated Name")
                .type(AggregateType.VM_40)
                .hasTemperatureSensors(false)
                .build();
        AggregateDto expectedDto = new AggregateDto(id, "Updated Name", AggregateType.VM_40, false);

        when(aggregateRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(aggregateRepository.save(any(Aggregate.class))).thenReturn(updatedEntity);
        when(aggregateMapper.toDto(updatedEntity)).thenReturn(expectedDto);

        AggregateDto result = aggregateService.updateAggregate(id, updateDto);

        assertThat(result.name()).isEqualTo("Updated Name");
        assertThat(result.type()).isEqualTo(AggregateType.VM_40);
        assertThat(result.hasTemperatureSensors()).isEqualTo(false);

        verify(aggregateRepository).save(argThat(aggregate ->
                aggregate.getName().equals("Updated Name") &&
                        aggregate.getType() == AggregateType.VM_40 &&
                        !aggregate.getHasTemperatureSensors()
        ));

    }

    @Test
    void findByType_WhenInvalidType_ShouldThrowResponseStatusException() {
        assertThatThrownBy(() -> aggregateService.findByType("INVALID_TYPE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant ru.practicum.enums.AggregateType.INVALID_TYPE");
    }

    @Test
    void getAllAggregates_WhenEmpty_ShouldReturnEmptyList() {
        when(aggregateRepository.findAll()).thenReturn(List.of());

        // When
        List<AggregateDto> result = aggregateService.getAllAggregates();

        // Then
        assertThat(result).isEmpty();
    }
}
