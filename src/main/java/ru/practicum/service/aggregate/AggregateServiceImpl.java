package ru.practicum.service.aggregate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.AggregateDTO;
import ru.practicum.enums.AggregateType;
import ru.practicum.error.exception.DuplicateAggregateException;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.ResponseStatusException;
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
    public AggregateDTO createAggregate(AggregateDTO aggregateDTO) {
        log.info("Создание агрегата {}", aggregateDTO);
        if (aggregateRepository.existsByNameIgnoreCase(aggregateDTO.getName())) {
            log.error("Агрегат с именем {} уже существует", aggregateDTO.getName());
            throw new DuplicateAggregateException("Агрегат с именем " + aggregateDTO.getName() + " уже существует");
        }
        Aggregate aggregate = aggregateRepository.save(aggregateMapper.toEntity(aggregateDTO));
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
    public List<AggregateDTO> getAllAggregates() {
        log.info("Получение списка всех агрегатов");
        return aggregateRepository.findAll().stream()
                .map(aggregateMapper::toDto)
                .toList();
    }

    @Override
    public AggregateDTO getAggregateById(Long id) {
        log.info("Получение агрегата с id {}", id);
        Aggregate aggregate = findAggregateById(id);
        log.info("Агрегат {} получен", aggregate);
        return aggregateMapper.toDto(aggregate);
    }

    @Transactional
    @Override
    public AggregateDTO updateAggregate(Long id, AggregateDTO aggregateDTO) {
        log.info("Обновление агрегата с id {}", id);
        Aggregate aggregate = findAggregateById(id);
        Optional.ofNullable(aggregateDTO.getName()).ifPresent(aggregate::setName);
        Optional.ofNullable(aggregateDTO.getType()).ifPresent(aggregate::setType);
        Optional.ofNullable(aggregateDTO.getHasTemperatureSensors()).ifPresent(aggregate::setHasTemperatureSensors);
        Aggregate updatedAggregate = aggregateRepository.save(aggregate);
        log.info("Агрегат {} обновлен", updatedAggregate);
        return aggregateMapper.toDto(updatedAggregate);
    }

    @Override
    public List<AggregateDTO> findByName(String name) {
        log.info("Поиск агрегата по имени {}", name);
        return aggregateRepository.findByNameContainsIgnoreCase(name).stream()
                .map(aggregateMapper::toDto)
                .toList();
    }

    @Override
    public List<AggregateDTO> findByType(String type) {
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
