package com.casino.quests.bot.service;

import com.casino.quests.bot.config.QuestBotProperties;
import com.casino.quests.bot.entity.QuestTaskEntity;
import com.casino.quests.bot.repo.QuestTaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final QuestTaskRepository questTaskRepository;
    private final QuestBotProperties properties;

    public boolean isAdmin(long telegramId, String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String normalized = username.trim().toLowerCase().replace("@", "");
        return properties.getAdmin().usernameList().contains(normalized);
    }

    @Transactional
    public QuestTaskEntity addNewTask(String description, long rewardCoins) {
        return questTaskRepository.save(new QuestTaskEntity(description, rewardCoins));
    }

    @Transactional
    public void deleteTask(long id) {
        questTaskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<QuestTaskEntity> getTasksPage(int page, int size) {
        return questTaskRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<QuestTaskEntity> listAll() {
        return questTaskRepository.findAll();
    }
}

