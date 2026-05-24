package com.casino.questbot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QuestBotProperties.class)
public class QuestBotConfig {}
