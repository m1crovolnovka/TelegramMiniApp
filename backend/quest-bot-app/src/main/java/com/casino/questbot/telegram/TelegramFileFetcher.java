package com.casino.questbot.telegram;

import java.net.URL;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramFileFetcher {

    private final String botToken;

    public TelegramFileFetcher(String botToken) {
        this.botToken = botToken;
    }

    public URL getFileUrl(TelegramLongPollingBot bot, String fileId) throws TelegramApiException {
        try {
            File file = bot.execute(GetFile.builder().fileId(fileId).build());
            return new URL("https://api.telegram.org/file/bot" + botToken + "/" + file.getFilePath());
        } catch (java.net.MalformedURLException e) {
            throw new TelegramApiException("Invalid file URL", e);
        }
    }
}
