package com.casino.admin.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Список Telegram username админов (без @). Env: CASINO_ADMIN_ALLOWED_USERNAMES=user1,user2
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "casino.admin")
public class AdminAccessProperties {

    private List<String> allowedUsernames = new ArrayList<>();
}
