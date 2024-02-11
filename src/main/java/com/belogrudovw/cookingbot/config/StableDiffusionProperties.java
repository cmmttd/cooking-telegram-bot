package com.belogrudovw.cookingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stable-diffusion")
public record StableDiffusionProperties(String apiUrl, String path, String token) {
}