package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.AggregateDTO;
import ru.practicum.service.aggregate.AggregateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/aggregates")
@Validated
public class AggregateController {

    private final AggregateService aggregateService;

    @PostMapping
    public ResponseEntity<AggregateDTO> createAggregate(@Valid @RequestBody AggregateDTO aggregateDTO) {
        log.info("POST /aggregates — создание агрегата: {}", aggregateDTO);
        AggregateDTO created = aggregateService.createAggregate(aggregateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAggregateById(@PathVariable @Min(1) Long id) {
        log.info("DELETE /aggregates/{} — удаление агрегата с id {}", id, id);
        aggregateService.deleteAggregateById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AggregateDTO>> getAllAggregates() {
        log.info("GET /aggregates — получение всех агрегатов");
        return ResponseEntity.ok(aggregateService.getAllAggregates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AggregateDTO> getAggregateById(@PathVariable @Min(1) Long id) {
        log.info("GET /aggregates/{} — получение агрегата с id {}", id, id);
        return ResponseEntity.ok(aggregateService.getAggregateById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AggregateDTO> updateAggregateById(@PathVariable Long id,
                                                            @RequestBody AggregateDTO aggregateDTO) {
        log.info("PATCH /aggregates/{} — обновление агрегата с id {}", id, id);
        AggregateDTO updated = aggregateService.updateAggregate(id, aggregateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<AggregateDTO>> getAggregatesByName(
            @RequestParam @NotBlank(message = "Имя агрегата не может быть пустым") String name) {
        log.info("GET /aggregates/search/by-name — получение агрегатов по имени {}", name);
        return ResponseEntity.ok(aggregateService.findByName(name));
    }

    @GetMapping("/search/by-type")
    public ResponseEntity<List<AggregateDTO>> getAggregatesByType(
            @RequestParam @NotBlank(message = "Тип агрегата не может быть пустым") String type) {
        log.info("GET /aggregates/search/by-type — получение агрегатов по типу {}", type);
        return ResponseEntity.ok(aggregateService.findByType(type));
    }
}
