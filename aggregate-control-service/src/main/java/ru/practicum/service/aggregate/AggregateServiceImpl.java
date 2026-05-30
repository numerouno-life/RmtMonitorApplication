package ru.practicum.service.aggregate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.AggregateDto;
import ru.practicum.enums.AggregateType;
import ru.practicum.exception.custom.DuplicateAggregateException;
import ru.practicum.exception.custom.EntityNotFoundException;
import ru.practicum.exception.custom.ResponseStatusException;
import ru.practicum.exception.custom.IllegalArgumentException;

import ru.practicum.mapper.AggregateMapper;
import ru.practicum.model.Aggregate;
import ru.practicum.repository.AggregateRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregateServiceImpl implements AggregateService {
    private final AggregateRepository aggregateRepository;
    private final AggregateMapper aggregateMapper;

    @Transactional
    @Override
    public AggregateDto createAggregate(AggregateDto aggregateDto) {
        log.info("Создание агрегата {}", aggregateDto);
        if (aggregateRepository.existsByNameIgnoreCase(aggregateDto.name())) {
            log.error("Агрегат с именем {} уже существует", aggregateDto.name());
            throw new DuplicateAggregateException("Агрегат с именем " + aggregateDto.name() + " уже существует");
        }
        if (aggregateDto.name() == null || aggregateDto.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        Aggregate aggregate = aggregateRepository.save(aggregateMapper.toEntity(aggregateDto));
        log.info("Агрегат {} создан", aggregate);
        return aggregateMapper.toDto(aggregate);
    }

    @Transactional
    @Override
    public void deleteAggregateById(Long id) {
        log.info("Удаление агрегата с id {}", id);
        if (!aggregateRepository.existsById(id)) {
            log.error("Агрегат с id {} не найден", id);
            throw new EntityNotFoundException("Агрегат с id " + id + " не найден");
        }
        aggregateRepository.deleteById(id);
        log.info("Агрегат с id {} удален", id);
    }

    @Override
    public List<AggregateDto> getAllAggregates() {
        log.info("Получение списка всех агрегатов");
        return aggregateRepository.findAll().stream()
                .map(aggregateMapper::toDto)
                .toList();
    }

    @Override
    public AggregateDto getAggregateById(Long id) {
        log.info("Получение агрегата с id {}", id);
        Aggregate aggregate = findAggregateById(id);
        log.info("Агрегат {} получен", aggregate);
        return aggregateMapper.toDto(aggregate);
    }

    @Transactional
    @Override
    public AggregateDto updateAggregate(Long id, AggregateDto aggregateDto) {
        log.info("Обновление агрегата с id {}", id);
        Aggregate aggregate = findAggregateById(id);
        Optional.ofNullable(aggregateDto.name()).ifPresent(aggregate::setName);
        Optional.ofNullable(aggregateDto.type()).ifPresent(aggregate::setType);
        Optional.ofNullable(aggregateDto.hasTemperatureSensors()).ifPresent(aggregate::setHasTemperatureSensors);
        Aggregate updatedAggregate = aggregateRepository.save(aggregate);
        log.info("Агрегат {} обновлен", updatedAggregate);
        return aggregateMapper.toDto(updatedAggregate);
    }

    @Override
    public List<AggregateDto> findByName(String name) {
        log.info("Поиск агрегата по имени {}", name);
        return aggregateRepository.findByNameContainsIgnoreCase(name).stream()
                .map(aggregateMapper::toDto)
                .toList();
    }

    @Override
    public List<AggregateDto> findByType(String type) {
        log.info("Поиск агрегата по типу {}", type);
        String normalizedType = type.toUpperCase().trim();
        AggregateType enumType;
        try {
            enumType = AggregateType.valueOf(normalizedType.toUpperCase());
            return aggregateRepository.findByType(enumType).stream()
                    .map(aggregateMapper::toDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException("Неизвестный тип агрегата: " + normalizedType);
        }
    }

    private Aggregate findAggregateById(Long aggregateId) {
        return aggregateRepository.findById(aggregateId)
                .orElseThrow(() -> {
                    log.error("Агрегат с id {} не найден", aggregateId);
                    return new EntityNotFoundException("Агрегат с id " + aggregateId + " не найден");
                });
    }
}
