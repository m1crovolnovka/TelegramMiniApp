package com.casino.quests.bot.config;

import com.casino.quests.bot.service.AdminService;
import com.casino.quests.bot.service.BotUserService;
import com.casino.quests.bot.service.QuestService;
import com.casino.quests.bot.telegram.EventManagerBot;
import com.casino.quests.bot.telegram.TelegramFileFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@ConditionalOnExpression("'${casino.quest-bot.telegram.token:}'.length() > 0")
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
            BotUserService botUserService,
            QuestService questService,
            AdminService adminService,
            TelegramFileFetcher fileFetcher) {
        return new EventManagerBot(
                props.getTelegram().getToken(),
                props.getTelegram().getUsername(),
                props.getMiniAppUrl(),
                botUserService,
                questService,
                adminService,
                fileFetcher);
    }

    @Bean
    public BotRegistrar botRegistrar(TelegramBotsApi api, EventManagerBot bot, QuestBotProperties props) {
        return new BotRegistrar(api, bot, props);
    }

    public static class BotRegistrar {
        public BotRegistrar(TelegramBotsApi api, EventManagerBot bot, QuestBotProperties props) {
            try {
                api.registerBot(bot);
                log.info(
                        "Quest Telegram bot registered as @{}",
                        props.getTelegram().getUsername().isBlank() ? "?" : props.getTelegram().getUsername());
            } catch (TelegramApiException e) {
                throw new IllegalStateException("Failed to register quest telegram bot", e);
            }
        }
    }
}
