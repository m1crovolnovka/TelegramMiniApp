package com.casino.config.web;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS — настройка в {@code application.yml} → {@code casino.cors}.
 *
 * <p>{@code allowed-origin-patterns} — маски для preview-деплоев Vercel ({@code *-xxx.vercel.app}).
 * <p>{@code allowed-origins} — точные origin без пути (только схема + хост + порт).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "casino.cors")
public class CorsProperties {

    /** Точные origins, например {@code http://localhost:5173} */
    private List<String> allowedOrigins = new ArrayList<>();

    /** Маски, например {@code https://telegram-mini-app-4njn*.vercel.app} */
    private List<String> allowedOriginPatterns = new ArrayList<>();
}
