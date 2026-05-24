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
        name = "partner_quest_completions",
        indexes = {@Index(name = "uq_partner_quest_external_id", columnList = "external_assignment_id", unique = true)})
@Getter
@Setter
@NoArgsConstructor
public class PartnerQuestCompletion extends BaseAuditableEntity {

    @Column(name = "external_assignment_id", nullable = false, unique = true, length = 64)
    private String externalAssignmentId;

    @Column(name = "user_low_id", nullable = false)
    private Long userLowId;

    @Column(name = "user_high_id", nullable = false)
    private Long userHighId;

    public PartnerQuestCompletion(String externalAssignmentId, long userLowId, long userHighId) {
        this.externalAssignmentId = externalAssignmentId;
        this.userLowId = userLowId;
        this.userHighId = userHighId;
    }
}
