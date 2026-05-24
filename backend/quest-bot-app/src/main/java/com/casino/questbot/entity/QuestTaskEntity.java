package com.casino.questbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quest_tasks")
@Getter
@Setter
@NoArgsConstructor
public class QuestTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String description;

    @Column(name = "reward_coins", nullable = false)
    private long rewardCoins;

    public QuestTaskEntity(String description, long rewardCoins) {
        this.description = description;
        this.rewardCoins = rewardCoins;
    }
}
