package com.casino.quests.repository;

import com.casino.quests.entity.UserQuest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

    Optional<UserQuest> findByUserIdAndQuestId(long userId, long questId);
}
