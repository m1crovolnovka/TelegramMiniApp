package com.casino.quests.bot.service;

import com.casino.quests.bot.entity.ProofType;
import com.casino.quests.bot.entity.QuestAssignmentEntity;
import com.casino.quests.bot.entity.QuestTaskEntity;
import com.casino.quests.bot.entity.TaskStatus;
import com.casino.quests.bot.entity.UserEntity;
import com.casino.quests.bot.integration.CasinoQuestBridge;
import com.casino.quests.bot.repo.QuestAssignmentRepository;
import com.casino.quests.bot.repo.QuestTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestTaskRepository tasks;
    private final QuestAssignmentRepository assignments;
    private final CasinoQuestBridge casinoQuestBridge;

    @Transactional(readOnly = true)
    public Optional<QuestAssignmentEntity> findActiveForUser(UserEntity user) {
        return assignments.findActiveForUser(user);
    }

    @Transactional
    public Optional<QuestAssignmentEntity> cancelActiveForUser(UserEntity user) {
        Optional<QuestAssignmentEntity> active = assignments.findActiveForUser(user);
        if (active.isEmpty()) {
            return Optional.empty();
        }
        QuestAssignmentEntity a = active.get();
        assignments.delete(a);
        return Optional.of(a);
    }

    @Transactional
    public CreateQuestResult createQuest(UserEntity initiator, UserEntity partner) {
        if (initiator.getId().equals(partner.getId())) {
            return CreateQuestResult.fail("Нельзя создать задание с самим собой.");
        }
        if (assignments.findActiveForUser(initiator).isPresent()) {
            return CreateQuestResult.fail(
                    "У вас уже есть активное задание. Сначала выполните его или откажитесь.");
        }
        if (assignments.findActiveForUser(partner).isPresent()) {
            return CreateQuestResult.fail("У выбранного участника уже есть активное задание.");
        }

        List<QuestTaskEntity> candidates = tasks.findAll();
        if (candidates.isEmpty()) {
            return CreateQuestResult.fail("В базе нет заданий. Админ должен добавить квесты.");
        }

        Set<Long> usedByInitiator = new HashSet<>(assignments.findTaskIdsEverUsedByUser(initiator));
        Set<Long> usedByPartner = new HashSet<>(assignments.findTaskIdsEverUsedByUser(partner));
        List<QuestTaskEntity> available =
                candidates.stream()
                        .filter(t -> !usedByInitiator.contains(t.getId()) && !usedByPartner.contains(t.getId()))
                        .toList();
        if (available.isEmpty()) {
            return CreateQuestResult.fail("Для вашей пары больше нет уникальных заданий.");
        }

        QuestTaskEntity chosen = available.get(ThreadLocalRandom.current().nextInt(available.size()));
        QuestAssignmentEntity saved = assignments.save(new QuestAssignmentEntity(initiator, partner, chosen));
        return CreateQuestResult.ok(saved);
    }

    @Transactional
    public CompleteProofResult submitProof(UserEntity user, String fileId, ProofType type) {
        Optional<QuestAssignmentEntity> activeOpt = assignments.findActiveForUser(user);
        if (activeOpt.isEmpty()) {
            return CompleteProofResult.fail("У вас нет активного задания.");
        }
        QuestAssignmentEntity a = activeOpt.get();
        if (a.getStatus() != TaskStatus.ASSIGNED) {
            return CompleteProofResult.fail("Это задание уже не принимает доказательства.");
        }
        a.setProofFileId(fileId);
        a.setProofType(type);
        a.setProofSubmittedBy(user);
        a.setCompletedAt(Instant.now());
        a.setStatus(TaskStatus.COMPLETED_PENDING_REVIEW);
        assignments.save(a);
        return CompleteProofResult.ok(a);
    }

    @Transactional
    public Optional<QuestAssignmentEntity> approveAssignment(long assignmentId) {
        QuestAssignmentEntity assignment =
                assignments.findById(assignmentId).orElseThrow(() -> new EntityNotFoundException("Not found"));
        if (assignment.getStatus() == TaskStatus.APPROVED) {
            return Optional.empty();
        }
        if (assignment.getStatus() != TaskStatus.COMPLETED_PENDING_REVIEW) {
            return Optional.empty();
        }
        assignment.setStatus(TaskStatus.APPROVED);
        assignment.setReviewedAt(Instant.now());
        assignments.save(assignment);

        casinoQuestBridge.completePartnerQuest(
                "bot-assignment-" + assignment.getId(),
                assignment.getUserA().getUsername(),
                assignment.getUserB().getUsername(),
                assignment.getTask().getRewardCoins());

        assignment.getUserA().incrementCompletedTasksCount();
        assignment.getUserB().incrementCompletedTasksCount();
        return Optional.of(assignment);
    }

    @Transactional
    public Optional<QuestAssignmentEntity> reject(long assignmentId) {
        Optional<QuestAssignmentEntity> opt = assignments.findById(assignmentId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        QuestAssignmentEntity a = opt.get();
        if (a.getStatus() != TaskStatus.COMPLETED_PENDING_REVIEW) {
            return Optional.of(a);
        }
        a.setStatus(TaskStatus.REJECTED);
        a.setReviewedAt(Instant.now());
        return Optional.of(assignments.save(a));
    }

    @Transactional
    public void markAdminNotified(QuestAssignmentEntity assignment) {
        assignment.setAdminNotified(true);
        assignments.save(assignment);
    }

    @Transactional(readOnly = true)
    public long countByStatus(TaskStatus status) {
        return assignments.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public Optional<QuestAssignmentEntity> findFirstPending() {
        return assignments.findFirstByStatusOrderByCreatedAtAsc(TaskStatus.COMPLETED_PENDING_REVIEW);
    }

    public record CreateQuestResult(boolean ok, String error, QuestAssignmentEntity assignment) {
        public static CreateQuestResult ok(QuestAssignmentEntity a) {
            return new CreateQuestResult(true, null, a);
        }

        public static CreateQuestResult fail(String msg) {
            return new CreateQuestResult(false, msg, null);
        }
    }

    public record CompleteProofResult(boolean ok, String error, QuestAssignmentEntity assignment) {
        public static CompleteProofResult ok(QuestAssignmentEntity a) {
            return new CompleteProofResult(true, null, a);
        }

        public static CompleteProofResult fail(String msg) {
            return new CompleteProofResult(false, msg, null);
        }
    }
}
