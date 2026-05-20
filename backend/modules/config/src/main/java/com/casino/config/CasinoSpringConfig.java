package com.casino.config;

import com.casino.config.jwt.JwtProperties;
import com.casino.config.telegram.TelegramProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, TelegramProperties.class})
public class CasinoSpringConfig {
}
