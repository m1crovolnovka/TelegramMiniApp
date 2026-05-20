package com.casino.quests.repository;

import com.casino.quests.entity.QuestSubmission;
import com.casino.quests.entity.QuestSubmission.QuestSubmissionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestSubmissionRepository extends JpaRepository<QuestSubmission, Long> {

    boolean existsByQuestIdAndUserIdAndSubmissionStatus(
            long questId, long userId, QuestSubmissionStatus status);

    List<QuestSubmission> findByUserIdOrderByIdDesc(long userId);

    Optional<QuestSubmission> findByIdAndSubmissionStatus(long id, QuestSubmissionStatus status);
}
