package com.casino.quests.bot.repo;

import com.casino.quests.bot.entity.QuestTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestTaskRepository extends JpaRepository<QuestTaskEntity, Long> {}

