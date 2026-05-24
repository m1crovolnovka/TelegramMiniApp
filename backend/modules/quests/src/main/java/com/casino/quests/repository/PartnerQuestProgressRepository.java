package com.casino.quests.repository;

import com.casino.quests.entity.PartnerQuestProgress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerQuestProgressRepository extends JpaRepository<PartnerQuestProgress, Long> {

    Optional<PartnerQuestProgress> findByUserLowIdAndUserHighId(long userLowId, long userHighId);
}
