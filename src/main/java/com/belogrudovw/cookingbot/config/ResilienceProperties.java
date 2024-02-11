package com.belogrudovw.cookingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resilience")
public record ResilienceProperties(Retry retry, int timeoutMinutes) {
    public record Retry(int count, int delaySeconds, double jitter) {
    }
}