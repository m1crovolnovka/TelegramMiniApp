package com.casino.quests.bot.config;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "casino.quest-bot")
public class QuestBotProperties {

    private final Telegram telegram = new Telegram();
    private final Admin admin = new Admin();
    private String miniAppUrl = "";

    @Getter
    @Setter
    public static class Telegram {
        private String token = "";
        private String username = "";
    }

    @Getter
    @Setter
    public static class Admin {
        /** Comma-separated Telegram usernames (without @) with bot admin rights. */
        private String usernames = "admin";

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
}
