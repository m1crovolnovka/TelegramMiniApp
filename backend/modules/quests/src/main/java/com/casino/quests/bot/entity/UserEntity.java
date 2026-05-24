package com.casino.quests.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "bot_users",
        indexes = {@Index(name = "ix_bot_users_username", columnList = "username", unique = true)})
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "telegram_chat_id", nullable = false)
    private Long telegramChatId;

    @Column(name = "completed_tasks_count", nullable = false)
    private int completedTasksCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UserEntity(String username, long telegramChatId) {
        this.username = username;
        this.telegramChatId = telegramChatId;
    }

    public void incrementCompletedTasksCount() {
        this.completedTasksCount++;
    }
}

