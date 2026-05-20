package com.casino.quests.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "user_quests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "quest_id"}))
@Getter
@Setter
@NoArgsConstructor
public class UserQuest extends BaseAuditableEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "quest_id", nullable = false)
    private Long questId;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    public UserQuest(Long userId, Long questId, boolean completed) {
        this.userId = userId;
        this.questId = questId;
        this.completed = completed;
    }
}
