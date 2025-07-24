package ru.practicum.service.notification;

import ru.practicum.dto.AlertDto;

public interface NotificationService {

    void sendAlert(AlertDto alertDto);
}
