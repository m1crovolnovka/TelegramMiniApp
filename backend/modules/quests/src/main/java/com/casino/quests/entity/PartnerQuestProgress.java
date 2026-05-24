package com.casino.quests.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "partner_quest_progress",
        indexes = {@Index(name = "uq_partner_quest_pair", columnList = "user_low_id,user_high_id", unique = true)})
@Getter
@Setter
@NoArgsConstructor
public class PartnerQuestProgress extends BaseAuditableEntity {

    @Column(name = "user_low_id", nullable = false)
    private Long userLowId;

    @Column(name = "user_high_id", nullable = false)
    private Long userHighId;

    @Column(name = "completed_count", nullable = false)
    private int completedCount;

    public PartnerQuestProgress(long userLowId, long userHighId) {
        this.userLowId = userLowId;
        this.userHighId = userHighId;
        this.completedCount = 0;
    }
}
