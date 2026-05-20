package com.casino.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "casino.jwt")
public class JwtProperties {

    /**
     * HS256 secret; must be strong in production.
     */
    private String secret = "";

    private long accessTokenValiditySeconds = 3600;
}
