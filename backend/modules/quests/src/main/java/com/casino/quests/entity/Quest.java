package com.casino.quests.entity;

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
@Table(name = "quests")
@Getter
@Setter
@NoArgsConstructor
public class Quest extends BaseAuditableEntity {

    @Column(nullable = false, length = 256)
    private String title;

    @Column(name = "reward_coins", nullable = false)
    private long rewardCoins;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_status", nullable = false, length = 32)
    private QuestStatus questStatus = QuestStatus.ACTIVE;

    public Quest(String title, long rewardCoins, QuestStatus questStatus) {
        this.title = title;
        this.rewardCoins = rewardCoins;
        this.questStatus = questStatus;
    }
}
