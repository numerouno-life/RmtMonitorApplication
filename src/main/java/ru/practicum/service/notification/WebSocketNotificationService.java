package ru.practicum.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.config.TemperatureUpdateListener;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService implements TemperatureUpdateListener {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onTemperatureUpdate(Long aggregateId, Double frontTemp, Double rearTemp) {
        try {
            messagingTemplate.convertAndSend(
                    "/topic/temperature/" + aggregateId,
                    Map.of(
                            "front", frontTemp != null ? frontTemp : Double.NaN,
                            "rear", rearTemp != null ? rearTemp : Double.NaN,
                            "timestamp", LocalDateTime.now().toString(),
                            "aggregateId", aggregateId
                    )
            );
        } catch (Exception e) {
            log.error("Failed to broadcast temperature update", e);
        }
    }
}
