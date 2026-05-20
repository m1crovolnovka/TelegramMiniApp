package com.casino.quests.repository;

import com.casino.quests.entity.Quest;
import com.casino.quests.entity.QuestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    List<Quest> findByQuestStatus(QuestStatus status);
}
