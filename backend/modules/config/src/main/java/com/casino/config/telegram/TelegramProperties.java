package com.casino.config.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "casino.telegram")
public class TelegramProperties {

    private String botToken = "";
    private String botUsername = "";
    /** HTTPS URL of Telegram Mini App (Casino). */
    private String miniAppUrl = "";
    /** Comma-separated Telegram user IDs with quest-bot admin rights. */
    private String questAdminIds = "";
    /** Comma-separated usernames for quest-bot admin (without @). */
    private String questAdminUsernames = "admin";
}
