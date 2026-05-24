package com.casino.config.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "casino.internal")
public class InternalApiProperties {

    private String apiKey = "dev-internal-key";
}
