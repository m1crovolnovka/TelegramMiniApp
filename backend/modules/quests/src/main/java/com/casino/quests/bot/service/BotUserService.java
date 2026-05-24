package com.casino.quests.bot.service;

import com.casino.quests.bot.config.QuestBotProperties;
import com.casino.quests.bot.entity.UserEntity;
import com.casino.quests.bot.integration.CasinoQuestBridge;
import com.casino.quests.bot.repo.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BotUserService {

    private final UserRepository userRepository;
    private final QuestBotProperties properties;
    private final CasinoQuestBridge casinoQuestBridge;

    @Transactional
    public UserEntity getOrCreateByTelegram(String username, long chatId) {
        String normalized = normalize(username);
        UserEntity user =
                userRepository
                        .findByUsernameIgnoreCase(normalized)
                        .map(
                                existing -> {
                                    existing.setTelegramChatId(chatId);
                                    return existing;
                                })
                        .orElseGet(() -> userRepository.save(new UserEntity(normalized, chatId)));
        casinoQuestBridge.ensureCasinoUser(normalized, chatId);
        return user;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> all() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(normalize(username));
    }

    @Transactional(readOnly = true)
    public List<Long> adminChatIds() {
        return properties.getAdmin().telegramIdList();
    }

    private static String normalize(String username) {
        return username.trim().toLowerCase().replace("@", "");
    }
}


