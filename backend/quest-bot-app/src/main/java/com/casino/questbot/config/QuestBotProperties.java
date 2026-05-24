package com.casino.questbot.config;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "quest-bot")
public class QuestBotProperties {

    private final Telegram telegram = new Telegram();
    private final Admin admin = new Admin();
    private final Casino casino = new Casino();

    @Getter
    @Setter
    public static class Telegram {
        private String token = "";
        private String username = "";
    }

    @Getter
    @Setter
    public static class Admin {
        private String telegramIds = "";
        private String usernames = "admin";

        public List<Long> telegramIdList() {
            if (telegramIds == null || telegramIds.isBlank()) {
                return List.of();
            }
            return Arrays.stream(telegramIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        }

        public List<String> usernameList() {
            if (usernames == null || usernames.isBlank()) {
                return List.of();
            }
            return Arrays.stream(usernames.split(","))
                    .map(s -> s.trim().toLowerCase().replace("@", ""))
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
    }

    @Getter
    @Setter
    public static class Casino {
        private String baseUrl = "http://localhost:8080";
        private String internalApiKey = "dev-internal-key";
    }
}
