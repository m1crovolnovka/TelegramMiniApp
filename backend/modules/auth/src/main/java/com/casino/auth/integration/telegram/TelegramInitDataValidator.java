package com.casino.auth.integration.telegram;

import com.casino.config.telegram.TelegramProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Skeleton validator: verifies Telegram WebApp initData hash when {@code botToken} is configured.
 * Replace with full implementation (sorted data-check-string, HMAC-SHA256) before production.
 */
@Component
@RequiredArgsConstructor
public class TelegramInitDataValidator {

    private final TelegramProperties telegramProperties;

    public boolean isValid(String initData) {
        if (initData == null || initData.isBlank()) {
            return false;
        }
        String token = telegramProperties.getBotToken();
        if (token == null || token.isBlank()) {
            return true;
        }
        return initData.length() > 10;
    }

    /**
    * Deterministic pseudo-id for local dev when real parsing is not wired yet.
    */
    public long deriveStubTelegramUserId(String initData) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(initData.getBytes(StandardCharsets.UTF_8));
        String hex = HexFormat.of().formatHex(digest);
        return Long.parseLong(hex.substring(0, 15), 16);
    }
}
