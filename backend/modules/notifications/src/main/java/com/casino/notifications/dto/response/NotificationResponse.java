package com.casino.notifications.dto.response;

import com.casino.notifications.entity.NotificationType;

public record NotificationResponse(long id, String message, NotificationType type, boolean read) {}
