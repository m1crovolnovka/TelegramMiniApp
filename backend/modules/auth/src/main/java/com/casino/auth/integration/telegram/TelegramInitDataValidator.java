package com.casino.auth.integration.telegram;

import com.casino.config.telegram.TelegramProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramInitDataValidator {

    private final TelegramProperties telegramProperties;
    private final ObjectMapper objectMapper;

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

    public Optional<TelegramUserPayload> parseUser(String initData) {
        if (initData == null || initData.isBlank()) {
            return Optional.empty();
        }
        for (String part : initData.split("&")) {
            if (part.startsWith("user=")) {
                try {
                    String json = URLDecoder.decode(part.substring(5), StandardCharsets.UTF_8);
                    JsonNode node = objectMapper.readTree(json);
                    long id = node.get("id").asLong();
                    String username = node.hasNonNull("username") ? node.get("username").asText() : null;
                    String firstName = node.hasNonNull("first_name") ? node.get("first_name").asText() : null;
                    return Optional.of(new TelegramUserPayload(id, username, firstName));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    /** Stable id: real Telegram user id from initData, or deterministic hash for dev tokens. */
    public TelegramUserPayload resolveUser(String initData) throws Exception {
        return parseUser(initData)
                .orElseGet(
                        () -> {
                            long stubId = 0;
                            try {
                                stubId = deriveStubTelegramUserId(initData);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return new TelegramUserPayload(stubId, null, "dev");
                        });
    }

    public long deriveStubTelegramUserId(String initData) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(initData.getBytes(StandardCharsets.UTF_8));
        String hex = HexFormat.of().formatHex(digest);
        return Long.parseLong(hex.substring(0, 15), 16);
    }

    public record TelegramUserPayload(long telegramId, String username, String firstName) {}
}
