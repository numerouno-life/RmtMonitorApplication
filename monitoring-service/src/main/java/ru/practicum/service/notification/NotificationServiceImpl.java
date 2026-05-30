package ru.practicum.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.dto.AlertDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendAlert(AlertDto alert) {
        log.info("Sending alert: {}", alert);
        messagingTemplate.convertAndSend("/topic/alerts", alert);
    }
}
