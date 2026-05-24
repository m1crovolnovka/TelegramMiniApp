package com.casino.auth.integration.telegram;

import com.casino.config.telegram.TelegramProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramInitDataValidator {

    private static final Pattern DEV_USERNAME_PATTERN =
            Pattern.compile("dev-([a-zA-Z0-9_]+)-", Pattern.CASE_INSENSITIVE);

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

    /** Real Telegram user from initData, or deterministic dev user from token string. */
    public TelegramUserPayload resolveUser(String initData) throws Exception {
        return parseUser(initData)
                .orElseGet(
                        () -> {
                            try {
                                long stubId = deriveStubTelegramUserId(initData);
                                String username = resolveDevUsername(initData);
                                return new TelegramUserPayload(stubId, username, username);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
    }

    public long deriveStubTelegramUserId(String initData) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(initData.getBytes(StandardCharsets.UTF_8));
        String hex = HexFormat.of().formatHex(digest);
        return Long.parseLong(hex.substring(0, 15), 16);
    }

    private String resolveDevUsername(String initData) {
        String lower = initData.toLowerCase();
        if (lower.contains("admin")) {
            return "admin";
        }
        if (lower.contains("player")) {
            return "player";
        }
        Matcher matcher = DEV_USERNAME_PATTERN.matcher(initData);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return "dev";
    }

    public record TelegramUserPayload(long telegramId, String username, String firstName) {}
}
