package com.casino.notifications.controller;

import com.casino.notifications.dto.response.NotificationResponse;
import com.casino.notifications.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> list(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return notificationService.listForUser(userId);
    }

    @PostMapping("/read/{id}")
    public void markRead(Authentication authentication, @PathVariable("id") long id) {
        long userId = (Long) authentication.getPrincipal();
        notificationService.markRead(userId, id);
    }
}
