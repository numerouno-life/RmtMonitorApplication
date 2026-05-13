package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.AggregateDto;

import java.util.List;

@FeignClient(name = "aggregate-control-service", path = "/aggregates")
public interface AggregateControlClient {

    @GetMapping("/{id}")
    AggregateDto getAggregateById(@PathVariable("id") Long id);

    @GetMapping
    List<AggregateDto> getAllAggregates();
}
