package com.belogrudovw.cookingbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class StableDiffusionApiConfiguration {

    @Bean
    public WebClient sdWebClient(StableDiffusionProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.apiUrl())
                .defaultHeader("Authorization", "Bearer " + properties.token())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "image/png")
                .build();
    }
}