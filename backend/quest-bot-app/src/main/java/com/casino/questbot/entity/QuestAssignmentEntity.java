package com.casino.questbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "quest_assignments",
        indexes = {
            @Index(name = "ix_assignments_status", columnList = "status"),
            @Index(name = "ix_assignments_user_a", columnList = "user_a_id,status"),
            @Index(name = "ix_assignments_user_b", columnList = "user_b_id,status")
        })
@Getter
@Setter
@NoArgsConstructor
public class QuestAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private UserEntity userA;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private UserEntity userB;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private QuestTaskEntity task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TaskStatus status = TaskStatus.ASSIGNED;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    private Instant completedAt;
    private Instant reviewedAt;

    @Column(length = 256)
    private String proofFileId;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ProofType proofType;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity proofSubmittedBy;

    @Column(name = "admin_notified", nullable = false)
    private boolean adminNotified;

    public QuestAssignmentEntity(UserEntity userA, UserEntity userB, QuestTaskEntity task) {
        this.userA = userA;
        this.userB = userB;
        this.task = task;
    }

    public boolean hasUser(UserEntity user) {
        return user.getId().equals(userA.getId()) || user.getId().equals(userB.getId());
    }

    public UserEntity other(UserEntity user) {
        return user.getId().equals(userA.getId()) ? userB : userA;
    }
}
