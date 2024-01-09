package com.belogrudovw.cookingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-ai")
public record OpenAiProperties(String apiUrl, String token, Conversation conversation) {
    public record Conversation(Models models, int temperature, int maxTokens) {
    }

    public record Models(String cheap, String wise) {
    }
}