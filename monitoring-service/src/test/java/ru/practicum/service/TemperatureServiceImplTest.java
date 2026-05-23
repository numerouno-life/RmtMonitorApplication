package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.controller.TemperatureWebSocketController;
import ru.practicum.dto.AggregateDto;
import ru.practicum.enums.AggregateType;
import ru.practicum.feign.AggregateControlClient;
import ru.practicum.modbus.ModbusClient;
import ru.practicum.model.TemperatureReading;
import ru.practicum.model.Threshold;
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
    private AggregateControlClient aggregateControlClient;

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
        Long aggregateId = 1L;
        AggregateDto aggregate = AggregateDto.builder()
                .id(aggregateId)
                .name("Test Aggregate")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();

        double frontTemp = 60.0;
        double rearTemp = 55.0;
        double[] mockTemps = {frontTemp, rearTemp};

        // Мокаем зависимости
        when(aggregateControlClient.getAggregateById(aggregateId)).thenReturn(aggregate);
        when(modbusClient.readTemperatures(2, 3)).thenReturn(mockTemps);

        // Act
        temperatureService.readAndStoreTemperatures(aggregateId);

        // Assert
        verify(modbusClient).readTemperatures(2, 3);

        // Проверяем сохранение TemperatureReading
        ArgumentCaptor<TemperatureReading> readingCaptor = ArgumentCaptor.forClass(TemperatureReading.class);
        verify(temperatureReadingRepository).save(readingCaptor.capture());
        TemperatureReading savedReading = readingCaptor.getValue();
        assertThat(savedReading.getFrontBearingTemp()).isEqualTo(frontTemp);
        assertThat(savedReading.getRearBearingTemp()).isEqualTo(rearTemp);
        assertThat(savedReading.getIsWarningTriggered()).isFalse();
        assertThat(savedReading.getIsAlarmTriggered()).isFalse();
        assertThat(savedReading.getAggregateId()).isEqualTo(aggregateId);

        // Проверяем, что Threshold НЕ сохраняется (нет предупреждений и аварий)
        verify(thresholdRepository, never()).save(any(Threshold.class));

        // Проверяем, что уведомления НЕ отправлялись
        verify(notificationService, never()).sendAlert(any());

        // Проверяем WebSocket
        verify(temperatureWebSocketController).broadcastTemperatureUpdate(eq(aggregateId), eq(frontTemp), eq(rearTemp));
    }

    @Test
    void readAndStoreTemperatures_WhenAlarmTemperature_ShouldSaveThreshold() throws IOException {
        Long aggregateId = 1L;
        AggregateDto aggregate = AggregateDto.builder()
                .id(aggregateId)
                .name("Test Aggregate")
                .type(AggregateType.VD_18)
                .hasTemperatureSensors(true)
                .build();

        double frontTemp = 85.0; // Аварийная температура (выше 80)
        double rearTemp = 55.0;
        double[] mockTemps = {frontTemp, rearTemp};

        // Мокаем зависимости
        when(aggregateControlClient.getAggregateById(aggregateId)).thenReturn(aggregate);
        when(modbusClient.readTemperatures(2, 3)).thenReturn(mockTemps);
        when(thresholdRepository.findByAggregateId(aggregateId)).thenReturn(Optional.empty());

        // Act
        temperatureService.readAndStoreTemperatures(aggregateId);

        // Assert
        // Проверяем, что Threshold был создан и сохранен
        ArgumentCaptor<Threshold> thresholdCaptor = ArgumentCaptor.forClass(Threshold.class);
        verify(thresholdRepository).save(thresholdCaptor.capture());
        Threshold savedThreshold = thresholdCaptor.getValue();
        assertThat(savedThreshold.getAggregateId()).isEqualTo(aggregateId);
        assertThat(savedThreshold.getWarningThreshold()).isEqualTo(75.0);
        assertThat(savedThreshold.getAlarmThreshold()).isEqualTo(80.0);
        assertThat(savedThreshold.getWarningTimestamp()).isEqualTo(LocalDateTime.MIN);
        assertThat(savedThreshold.getAlarmTimestamp()).isNotEqualTo(LocalDateTime.MIN); // Должен быть установлен

        // Проверяем, что аварийное уведомление отправлено
        verify(notificationService, atLeastOnce()).sendAlert(any());
    }
}
