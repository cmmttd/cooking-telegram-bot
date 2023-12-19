package com.belogrudovw.cookingbot.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(String telegramApiUrl, String webhookUrl, Bot bot) {
    public record Bot(String name, String token, String path) {
    }
}