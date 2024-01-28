package com.belogrudovw.cookingbot.config;

import com.belogrudovw.cookingbot.util.CustomUriBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramWebhookRegistrar {

    TelegramProperties properties;
    WebClient telegramWebClient;

    @PostConstruct
    void setup() {
        resetWebhook()
                .then(Mono.defer(this::registerWebhook))
                .doOnSuccess(this::logSuccess)
                .subscribe();
    }

    @PreDestroy
    void releaseResources() {
        resetWebhook()
                .subscribe();
    }

    private Mono<String> resetWebhook() {
        return telegramWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/deleteWebhook")
                        .queryParam("drop_pending_updates", true)
                        .build())
                .exchangeToMono(resp -> resp.bodyToMono(String.class))
                .doOnError(err -> log.error("Telegram reset webhook failed", err));
    }

    private Mono<String> registerWebhook() {
        String uri = CustomUriBuilder.builder()
                .path("/setWebhook")
                .queryParam("url", properties.webhookUrl() + properties.bot().path())
                .build();
        return telegramWebClient.post()
                .uri(uri)
                .exchangeToMono(resp -> resp.bodyToMono(String.class))
                .doOnError(err -> log.error("Telegram registration webhook failed"));
    }

    private void logSuccess(String ignored) {
        String actualWebhookPath = properties.webhookUrl() + properties.bot().path();
        log.info("Telegram webhook for {} successfully registered: {}", properties.bot().name(), actualWebhookPath);
    }
}