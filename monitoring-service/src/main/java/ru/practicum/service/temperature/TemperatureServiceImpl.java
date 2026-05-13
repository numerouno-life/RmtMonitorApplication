package ru.practicum.service.temperature;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.TemperatureWebSocketController;
import ru.practicum.dto.AggregateDto;
import ru.practicum.dto.AlertDto;
import ru.practicum.dto.TemperatureReadingDto;
import ru.practicum.enums.AggregateType;
import ru.practicum.feign.AggregateControlClient;
import ru.practicum.modbus.ModbusClient;
import ru.practicum.model.TemperatureReading;
import ru.practicum.model.Threshold;
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
    private final ThresholdRepository thresholdRepository;
    private final TemperatureReadingRepository temperatureReadingRepository;
    private final ModbusClient modbusClient;
    private final NotificationServiceImpl notificationService;
    private final TemperatureWebSocketController webSocketController;
    private final AggregateControlClient aggregateClient;

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
        AggregateDto aggregateDto;
        try {
            aggregateDto = aggregateClient.getAggregateById(aggregateId);
        } catch (FeignException.NotFound e) {
            log.error("Aggregate with id {} not found in control-service", aggregateId);
            return; //TODO: выбросить бизнес-исключение
        }
        if (!aggregateDto.hasTemperatureSensors()) {
            log.warn("Aggregate {} does not have temperature sensors", aggregateId);
            return;
        }
        AggregateType aggregateType = aggregateDto.type();
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
            sendAlarmNotification(aggregateDto, front, rear);
        } else if (isWarning) {
            sendWarningNotification(aggregateDto, front, rear);
        }

        saveTemperatureReading(aggregateDto,
                front != null ? front : Double.NaN,
                rear != null ? rear : Double.NaN,
                isWarning, isAlarm);

        broadcastTemperatureUpdate(aggregateId, front, rear);
        // Обновляем уставку
        Threshold threshold = thresholdRepository.findByAggregateId(aggregateId)
                .orElseGet(() -> Threshold.builder()
                        .aggregateDto(aggregateDto)
                        .warningThreshold(warningLimit)
                        .alarmThreshold(alarmLimit)
                        .warningTimestamp(LocalDateTime.MIN)
                        .alarmTimestamp(LocalDateTime.MIN)
                        .build());

        saveThresholdEvent(aggregateDto, threshold, isWarning, isAlarm);
    }

    @Override
    public List<TemperatureReadingDto> getLatestReadings() {
        return List.of();
    }

    @Override
    public List<TemperatureReadingDto> getReadingForAggregate(Long aggregateId) {
        return List.of();
    }

    private void saveTemperatureReading(AggregateDto aggregateDto, double front, double rear,
                                        boolean isWarning, boolean isAlarm) {
        TemperatureReading reading = TemperatureReading.builder()
                .aggregateId(aggregateDto.id())
                .frontBearingTemp(front)
                .rearBearingTemp(rear)
                .isWarningTriggered(isWarning)
                .isAlarmTriggered(isAlarm)
                .build();
        temperatureReadingRepository.save(reading);
    }

    private void saveThresholdEvent(AggregateDto aggregateDto, Threshold threshold,
                                    boolean isWarning, boolean isAlarm) {
        Threshold update = threshold.toBuilder()
                .aggregateDto(aggregateDto)
                .warningTimestamp(isWarning ? LocalDateTime.now() : threshold.getWarningTimestamp())
                .alarmTimestamp(isAlarm ? LocalDateTime.now() : threshold.getAlarmTimestamp())
                .build();
        thresholdRepository.save(update);
    }

    private void sendAlarmNotification(AggregateDto aggregateDto, Double front, Double rear) {
        notificationService.sendAlert(
                AlertDto.builder()
                        .message("АВАРИЙНАЯ ТЕМПЕРАТУРА!")
                        .level(AlertDto.AlertLevel.ALARM)
                        .aggregateId(aggregateDto.id())
                        .frontTemperature(front)
                        .rearTemperature(rear)
                        .build()
        );
    }

    private void sendWarningNotification(AggregateDto aggregateDto, Double front, Double rear) {
        notificationService.sendAlert(
                AlertDto.builder()
                        .message("ПРЕДУПРЕДИТЕЛЬНАЯ ТЕМПЕРАТУРА!")
                        .level(AlertDto.AlertLevel.WARNING)
                        .aggregateId(aggregateDto.id())
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
