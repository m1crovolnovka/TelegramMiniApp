package com.casino.questbot.config;

import com.casino.questbot.service.AdminService;
import com.casino.questbot.service.QuestService;
import com.casino.questbot.service.UserService;
import com.casino.questbot.telegram.EventManagerBot;
import com.casino.questbot.telegram.TelegramFileFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@ConditionalOnExpression("'${quest-bot.telegram.token:}' != ''")
public class BotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public TelegramFileFetcher telegramFileFetcher(QuestBotProperties props) {
        return new TelegramFileFetcher(props.getTelegram().getToken());
    }

    @Bean
    public EventManagerBot eventManagerBot(
            QuestBotProperties props,
            UserService userService,
            QuestService questService,
            AdminService adminService,
            TelegramFileFetcher fileFetcher) {
        return new EventManagerBot(
                props.getTelegram().getToken(),
                props.getTelegram().getUsername(),
                userService,
                questService,
                adminService,
                fileFetcher);
    }

    @Bean
    public BotRegistrar botRegistrar(TelegramBotsApi api, EventManagerBot bot) {
        return new BotRegistrar(api, bot);
    }

    public static class BotRegistrar {
        public BotRegistrar(TelegramBotsApi api, EventManagerBot bot) {
            try {
                api.registerBot(bot);
            } catch (TelegramApiException e) {
                throw new IllegalStateException("Failed to register telegram bot", e);
            }
        }
    }
}
