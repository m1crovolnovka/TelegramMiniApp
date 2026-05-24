package com.casino.config;

import com.casino.config.internal.InternalApiProperties;
import com.casino.config.jwt.JwtProperties;
import com.casino.config.telegram.TelegramProperties;
import com.casino.config.web.CorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    JwtProperties.class,
    TelegramProperties.class,
    CorsProperties.class,
    InternalApiProperties.class
})
public class CasinoSpringConfig {
}
