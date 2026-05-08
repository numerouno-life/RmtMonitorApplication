package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class TemperatureWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastTemperatureUpdate(Long aggregateId, Double frontTemp, Double rearTemp) {
        messagingTemplate.convertAndSend(
                "/topic/temperature/" + aggregateId,
                Map.of(
                        "front", frontTemp,
                        "rear", rearTemp,
                        "timestamp", LocalDateTime.now().toString(),
                        "aggregateId", aggregateId
                )
        );
    }
}
