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
@Table(name = "quest_submissions")
@Getter
@Setter
@NoArgsConstructor
public class QuestSubmission extends BaseAuditableEntity {

    public enum QuestSubmissionStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Column(name = "quest_id", nullable = false)
    private Long questId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", nullable = false, length = 32)
    private QuestSubmissionStatus submissionStatus = QuestSubmissionStatus.PENDING;

    @Column(length = 2048)
    private String proofText;

    public QuestSubmission(Long questId, Long userId, String proofText) {
        this.questId = questId;
        this.userId = userId;
        this.proofText = proofText;
    }
}
