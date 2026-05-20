package com.casino.notifications.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 32)
    private NotificationType notificationType = NotificationType.SYSTEM;

    @Column(nullable = false, length = 512)
    private String message;

    @Column(name = "read_flag", nullable = false)
    private boolean readFlag;

    public Notification(Long userId, NotificationType notificationType, String message) {
        this.userId = userId;
        this.notificationType = notificationType;
        this.message = message;
    }
}
