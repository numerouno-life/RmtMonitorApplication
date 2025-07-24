package ru.practicum.service.temperature;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.repository.AggregateRepository;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemperatureMonitoringServiceImpl implements TemperatureMonitoringService {
    private final TemperatureService temperatureService;
    private final AggregateRepository aggregateRepository;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<Long, ScheduledFuture<?>> monitoringTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        aggregateRepository.findAll().forEach(agg -> {
            if (agg.getHasTemperatureSensors()) {
                startMonitoring(agg.getId());
            }
        });
    }

    @Override
    public void startMonitoring(Long aggregateId) {
        if (monitoringTasks.containsKey(aggregateId)) {
            return;
        }

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() ->
                temperatureService.readAndStoreTemperatures(aggregateId), 0, 5, TimeUnit.SECONDS);

        monitoringTasks.put(aggregateId, task);
        log.info("Started monitoring for aggregate {}", aggregateId);
    }

    @Override
    public void stopMonitoring(Long aggregateId) {
        ScheduledFuture<?> task = monitoringTasks.remove(aggregateId);
        if (task != null) {
            task.cancel(false);
            log.info("Stopped monitoring for aggregate {}", aggregateId);
        }
    }

    @Override
    public boolean isMonitoring(Long aggregateId) {
        return monitoringTasks.containsKey(aggregateId);
    }
}