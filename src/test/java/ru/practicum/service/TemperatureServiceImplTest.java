package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.controller.TemperatureWebSocketController;
import ru.practicum.enums.AggregateType;
import ru.practicum.modbus.ModbusClient;
import ru.practicum.model.Aggregate;
import ru.practicum.model.TemperatureReading;
import ru.practicum.model.Threshold;
import ru.practicum.repository.AggregateRepository;
import ru.practicum.repository.TemperatureReadingRepository;
import ru.practicum.repository.ThresholdRepository;
import ru.practicum.service.notification.NotificationService;
import ru.practicum.service.temperature.TemperatureServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemperatureServiceImplTest {

    @InjectMocks
    private TemperatureServiceImpl temperatureService;

    @Mock
    private AggregateRepository aggregateRepository;

    @Mock
    private ThresholdRepository thresholdRepository;

    @Mock
    private TemperatureReadingRepository temperatureReadingRepository;

    @Mock
    private ModbusClient modbusClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TemperatureWebSocketController temperatureWebSocketController;

    @Test
    void readAndStoreTemperatures_WhenNormalTemperatures_ShouldSaveReadingAndBroadcast() throws IOException {
        // Arrange
        Long aggregateId = 1L;
        Aggregate aggregate = Aggregate.builder()
                .id(aggregateId)
                .name("Test Aggregate")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();

        double frontTemp = 60.0;
        double rearTemp = 55.0;
        double[] mockTemps = {frontTemp, rearTemp};

        // Мокаем зависимости
        when(aggregateRepository.findById(aggregateId)).thenReturn(Optional.of(aggregate));
        when(modbusClient.readTemperatures(2, 3)).thenReturn(mockTemps); // aggregateId * 2 = 2, +1 = 3
        when(thresholdRepository.findByAggregateId(aggregateId)).thenReturn(Optional.empty());

        // Act
        temperatureService.readAndStoreTemperatures(aggregateId);

        // Assert
        // Проверяем, что температура была прочитана
        verify(modbusClient).readTemperatures(2, 3);

        // Проверяем, что чтение было сохранено (без предупреждений/аварий)
        // ArgumentCaptor для проверки аргументов save
        ArgumentCaptor<TemperatureReading> readingCaptor = ArgumentCaptor.forClass(TemperatureReading.class);
        verify(temperatureReadingRepository).save(readingCaptor.capture());
        TemperatureReading savedReading = readingCaptor.getValue();
        assertThat(savedReading.getFrontBearingTemp()).isEqualTo(frontTemp);
        assertThat(savedReading.getRearBearingTemp()).isEqualTo(rearTemp);
        assertThat(savedReading.getIsWarningTriggered()).isFalse();
        assertThat(savedReading.getIsAlarmTriggered()).isFalse();
        assertThat(savedReading.getAggregate()).isEqualTo(aggregate);

        // Проверяем, что уставка была создана/обновлена
        ArgumentCaptor<Threshold> thresholdCaptor = ArgumentCaptor.forClass(Threshold.class);
        verify(thresholdRepository).save(thresholdCaptor.capture());
        Threshold savedThreshold = thresholdCaptor.getValue();
        assertThat(savedThreshold.getWarningThreshold()).isEqualTo(75.0); // предупредительная температура
        assertThat(savedThreshold.getAlarmThreshold()).isEqualTo(80.0);   // аварийная температура
        // Проверяем, что timestamps НЕ обновились, так как нет предупреждений/аварий
        assertThat(savedThreshold.getWarningTimestamp()).isEqualTo(LocalDateTime.MIN);
        assertThat(savedThreshold.getAlarmTimestamp()).isEqualTo(LocalDateTime.MIN);

        // Проверяем, что уведомления НЕ отправлялись
        verify(notificationService, never()).sendAlert(any());

        // Проверяем, что данные были отправлены по WebSocket
        verify(temperatureWebSocketController).broadcastTemperatureUpdate(eq(aggregateId), eq(frontTemp), eq(rearTemp));
    }
}
