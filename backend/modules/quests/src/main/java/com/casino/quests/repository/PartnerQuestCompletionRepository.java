package com.casino.quests.repository;

import com.casino.quests.entity.PartnerQuestCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerQuestCompletionRepository extends JpaRepository<PartnerQuestCompletion, Long> {

    boolean existsByExternalAssignmentId(String externalAssignmentId);
}
