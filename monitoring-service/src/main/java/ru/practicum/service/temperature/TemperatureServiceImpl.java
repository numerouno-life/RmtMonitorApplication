package ru.practicum.service.temperature;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.TemperatureWebSocketController;
import ru.practicum.dto.AlertDto;
import ru.practicum.dto.TemperatureReadingDTO;
import ru.practicum.enums.AggregateType;
import ru.practicum.modbus.ModbusClient;
import ru.practicum.model.Aggregate;
import ru.practicum.model.TemperatureReading;
import ru.practicum.model.Threshold;
import ru.practicum.repository.AggregateRepository;
import ru.practicum.repository.TemperatureReadingRepository;
import ru.practicum.repository.ThresholdRepository;
import ru.practicum.service.notification.NotificationServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemperatureServiceImpl implements TemperatureService {
    private final AggregateRepository aggregateRepository;
    private final ThresholdRepository thresholdRepository;
    private final TemperatureReadingRepository temperatureReadingRepository;
    private final ModbusClient modbusClient;
    private final NotificationServiceImpl notificationService;
    private final TemperatureWebSocketController webSocketController;

    private static final Map<AggregateType, Double> WARNING_LIMITS = Map.of(
            AggregateType.VD_18, 75.0,
            AggregateType.VM_40, 70.0
    );

    private static final Map<AggregateType, Double> ALARM_LIMITS = Map.of(
            AggregateType.VD_18, 80.0,
            AggregateType.VM_40, 75.0
    );

    @Transactional
    @Override
    public void readAndStoreTemperatures(Long aggregateId) {
        log.info("Reading temperature for aggregate {}", aggregateId);
        Aggregate aggregate = findAggregateById(aggregateId);
        if (!aggregate.getHasTemperatureSensors()) {
            log.warn("Aggregate {} does not have temperature sensors", aggregateId);
            return;
        }
        AggregateType aggregateType = aggregate.getType();
        double warningLimit = WARNING_LIMITS.get(aggregateType);
        double alarmLimit = ALARM_LIMITS.get(aggregateType);

        double[] temps;
        try {
            temps = modbusClient.readTemperatures(aggregateId.intValue() * 2, aggregateId.intValue() * 2 + 1);
        } catch (IOException e) {
            log.error("❌ Error reading temperatures for aggregate {}: {}", aggregateId, e.getMessage());
            return;
        }

        Double front = Double.isNaN(temps[0]) ? null : temps[0];
        Double rear = Double.isNaN(temps[1]) ? null : temps[1];
        boolean isWarning = (front != null && front >= warningLimit) || (rear != null && rear >= warningLimit);
        boolean isAlarm = (front != null && front >= alarmLimit) || (rear != null && rear >= alarmLimit);

        // Активировать сигнальные лампы и звук
        if (isAlarm) {
            sendAlarmNotification(aggregate, front, rear);
        } else if (isWarning) {
            sendWarningNotification(aggregate, front, rear);
        }

        saveTemperatureReading(aggregate,
                front != null ? front : Double.NaN,
                rear != null ? rear : Double.NaN,
                isWarning, isAlarm);

        broadcastTemperatureUpdate(aggregateId, front, rear);
        // Обновляем уставку
        Threshold threshold = thresholdRepository.findByAggregateId(aggregateId)
                .orElseGet(() -> Threshold.builder()
                        .aggregate(aggregate)
                        .warningThreshold(warningLimit)
                        .alarmThreshold(alarmLimit)
                        .warningTimestamp(LocalDateTime.MIN)
                        .alarmTimestamp(LocalDateTime.MIN)
                        .build());

        saveThresholdEvent(aggregate, threshold, isWarning, isAlarm);
    }

    @Override
    public List<TemperatureReadingDTO> getLatestReadings() {
        return List.of();
    }

    @Override
    public List<TemperatureReadingDTO> getReadingForAggregate(Long aggregateId) {
        return List.of();
    }

    private Aggregate findAggregateById(Long aggregateId) {
        return aggregateRepository.findById(aggregateId)
                .orElseThrow(() -> {
                    log.error("Aggregate with id {} not found", aggregateId);
                    return new EntityNotFoundException("Aggregate with id " + aggregateId + " not found");
                });
    }

    private void saveTemperatureReading(Aggregate aggregate, double front, double rear,
                                        boolean isWarning, boolean isAlarm) {
        TemperatureReading reading = TemperatureReading.builder()
                .aggregate(aggregate)
                .frontBearingTemp(front)
                .rearBearingTemp(rear)
                .isWarningTriggered(isWarning)
                .isAlarmTriggered(isAlarm)
                .build();
        temperatureReadingRepository.save(reading);
    }

    private void saveThresholdEvent(Aggregate aggregate, Threshold threshold, boolean isWarning, boolean isAlarm) {
        Threshold update = threshold.toBuilder()
                .warningTimestamp(isWarning ? LocalDateTime.now() : threshold.getWarningTimestamp())
                .alarmTimestamp(isAlarm ? LocalDateTime.now() : threshold.getAlarmTimestamp())
                .build();
        thresholdRepository.save(update);
    }

    private void sendAlarmNotification(Aggregate aggregate, Double front, Double rear) {
        notificationService.sendAlert(
                AlertDto.builder()
                        .message("АВАРИЙНАЯ ТЕМПЕРАТУРА!")
                        .level(AlertDto.AlertLevel.ALARM)
                        .aggregateId(aggregate.getId())
                        .frontTemperature(front)
                        .rearTemperature(rear)
                        .build()
        );
    }

    private void sendWarningNotification(Aggregate aggregate, Double front, Double rear) {
        notificationService.sendAlert(
                AlertDto.builder()
                        .message("ПРЕДУПРЕДИТЕЛЬНАЯ ТЕМПЕРАТУРА!")
                        .level(AlertDto.AlertLevel.WARNING)
                        .aggregateId(aggregate.getId())
                        .frontTemperature(front)
                        .rearTemperature(rear)
                        .build()
        );
    }

    private void broadcastTemperatureUpdate(Long aggregateId, Double front, Double rear) {
        try {
            webSocketController.broadcastTemperatureUpdate(
                    aggregateId,
                    front != null ? front : Double.NaN,
                    rear != null ? rear : Double.NaN
            );
        } catch (Exception e) {
            log.error("Failed to broadcast temperature update", e);
        }
    }
}
