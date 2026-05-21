package com.casino.config.web;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS — все разрешённые origins в {@code application.yml} → {@code casino.cors.allowed-origins}.
 * Переопределение через env: {@code CASINO_CORS_ALLOWED_ORIGINS=https://a.com,http://localhost:5173}
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "casino.cors")
public class CorsProperties {

    private List<String> allowedOrigins = new ArrayList<>();
}
