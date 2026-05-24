package com.casino.questbot.service;

import com.casino.questbot.config.QuestBotProperties;
import com.casino.questbot.entity.UserEntity;
import com.casino.questbot.integration.CasinoApiClient;
import com.casino.questbot.repo.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final QuestBotProperties properties;
    private final CasinoApiClient casinoApiClient;

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
        casinoApiClient.ensureUser(normalized, chatId);
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
