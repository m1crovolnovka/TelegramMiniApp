package com.casino.config.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = corsProperties.getAllowedOrigins();
        List<String> patterns = corsProperties.getAllowedOriginPatterns();
        if (CollectionUtils.isEmpty(origins) && CollectionUtils.isEmpty(patterns)) {
            throw new IllegalStateException(
                    "Configure casino.cors.allowed-origins and/or casino.cors.allowed-origin-patterns in application.yml");
        }

        CorsConfiguration c = new CorsConfiguration();
        if (!CollectionUtils.isEmpty(origins)) {
            c.setAllowedOrigins(origins);
        }
        if (!CollectionUtils.isEmpty(patterns)) {
            c.setAllowedOriginPatterns(patterns);
        }
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        c.setExposedHeaders(List.of("Authorization"));
        c.setAllowCredentials(true);
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
