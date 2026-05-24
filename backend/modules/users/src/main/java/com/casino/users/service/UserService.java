package com.casino.users.service;

import com.casino.economy.api.EconomyPort;
import com.casino.users.entity.User;
import com.casino.users.entity.UserRole;
import com.casino.users.exception.UserNotFoundException;
import com.casino.users.repository.UserRepository;
import com.casino.users.util.UsernameNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EconomyPort economyPort;

    @Transactional
    public User findOrCreateByUsername(String username, Long telegramId) {
        String normalized = UsernameNormalizer.normalize(username);
        return userRepository
                .findByUsernameIgnoreCase(normalized)
                .map(user -> {
                    economyPort.ensureWallet(user.getId());
                    if (telegramId != null) {
                        user.setTelegramId(telegramId);
                    }
                    return user;
                })
                .orElseGet(() -> {
                    long tgId = telegramId != null ? telegramId : 0L;
                    User u = userRepository.save(new User(tgId, normalized, UserRole.USER));
                    economyPort.ensureWallet(u.getId());
                    return u;
                });
    }

    @Transactional(readOnly = true)
    public User requireById(long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public User requireByUsername(String username) {
        String normalized = UsernameNormalizer.normalize(username);
        return userRepository
                .findByUsernameIgnoreCase(normalized)
                .orElseThrow(UserNotFoundException::new);
    }
}
