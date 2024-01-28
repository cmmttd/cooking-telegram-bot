package com.belogrudovw.cookingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-ai")
public record OpenAiProperties(Url url, String token, Conversation conversation) {
    public record Url(String base, String version, String path) {
    }

    public record Conversation(Models models, String temperature, String maxTokens) {
    }

    public record Models(String cheap, String wise) {
    }
}