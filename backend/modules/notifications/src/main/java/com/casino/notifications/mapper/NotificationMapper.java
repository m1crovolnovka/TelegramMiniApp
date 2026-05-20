package com.casino.notifications.mapper;

import com.casino.notifications.dto.response.NotificationResponse;
import com.casino.notifications.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getMessage(), n.getNotificationType(), n.isReadFlag());
    }
}
