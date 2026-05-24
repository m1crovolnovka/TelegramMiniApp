package com.casino.auth.service;

import com.casino.auth.dto.response.TokenResponse;
import com.casino.auth.exception.AuthException;
import com.casino.auth.integration.telegram.TelegramInitDataValidator;
import com.casino.auth.mapper.AuthMapper;
import com.casino.config.jwt.JwtService;
import com.casino.users.entity.User;
import com.casino.users.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TelegramInitDataValidator telegramInitDataValidator;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @Transactional
    public TokenResponse authenticateTelegram(String initData) {
        if (!telegramInitDataValidator.isValid(initData)) {
            throw new AuthException("Invalid Telegram initData");
        }
        TelegramInitDataValidator.TelegramUserPayload tg;
        try {
            tg = telegramInitDataValidator.resolveUser(initData);
        } catch (Exception e) {
            throw new AuthException("Cannot parse Telegram user");
        }
        if (tg.username() == null || tg.username().isBlank()) {
            throw new AuthException("Telegram username is required. Set it in Telegram settings.");
        }
        User user = userService.findOrCreateByUsername(tg.username(), tg.telegramId());
        String token = jwtService.createAccessToken(user.getId(), List.of(user.getRole().name()));
        return authMapper.toToken(token);
    }
}
