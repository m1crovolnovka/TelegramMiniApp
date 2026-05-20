package com.casino.notifications.service;

import com.casino.notifications.dto.response.NotificationResponse;
import com.casino.notifications.entity.Notification;
import com.casino.notifications.mapper.NotificationMapper;
import com.casino.notifications.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationResponse> listForUser(long userId) {
        return notificationRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional
    public void markRead(long userId, long notificationId) {
        Notification n =
                notificationRepository
                        .findById(notificationId)
                        .filter(x -> x.getUserId().equals(userId))
                        .orElseThrow();
        n.setReadFlag(true);
    }
}
