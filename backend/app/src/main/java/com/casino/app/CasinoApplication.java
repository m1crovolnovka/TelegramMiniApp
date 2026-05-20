package com.casino.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.casino")
@EntityScan(basePackages = "com.casino")
@EnableJpaRepositories(basePackages = "com.casino")
public class CasinoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasinoApplication.class, args);
    }
}
