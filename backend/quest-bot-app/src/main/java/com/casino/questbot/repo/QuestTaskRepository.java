package com.casino.questbot.repo;

import com.casino.questbot.entity.QuestTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestTaskRepository extends JpaRepository<QuestTaskEntity, Long> {}
