package com.casino.users.service;

import com.casino.economy.api.EconomyPort;
import com.casino.users.entity.User;
import com.casino.users.entity.UserRole;
import com.casino.users.exception.UserNotFoundException;
import com.casino.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EconomyPort economyPort;

    @Transactional
    public User findOrCreateByTelegram(long telegramId, String username) {
        return userRepository
                .findByTelegramId(telegramId)
                .map(user -> {
                    economyPort.ensureWallet(user.getId());
                    return user;
                })
                .orElseGet(() -> {
                    User u = userRepository.save(new User(telegramId, username, UserRole.USER));
                    economyPort.ensureWallet(u.getId());
                    return u;
                });
    }

    @Transactional(readOnly = true)
    public User requireById(long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }
}
