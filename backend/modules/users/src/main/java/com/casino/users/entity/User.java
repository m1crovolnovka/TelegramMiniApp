package com.casino.users.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "users",
        indexes = {
            @Index(name = "idx_users_telegram_id", columnList = "telegram_id", unique = true),
            @Index(name = "idx_users_username", columnList = "username", unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseAuditableEntity {

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    public User(Long telegramId, String username, UserRole role) {
        this.telegramId = telegramId;
        this.username = username;
        this.role = role;
    }
}
