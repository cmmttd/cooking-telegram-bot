package com.belogrudovw.cookingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(String apiUrl, String webhookUrl, Bot bot) {
    public record Bot(String name, String token, String path) {
    }
}