package com.belogrudovw.cookingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "recovery")
public record RecoveryProperties(boolean isNeeded, String defaultRecipesPath, String chatsPath) {
}