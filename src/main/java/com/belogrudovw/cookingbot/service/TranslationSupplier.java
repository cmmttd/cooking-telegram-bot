package com.belogrudovw.cookingbot.service;

import reactor.core.publisher.Mono;

public interface TranslationSupplier {
    Mono<String> getTranslation(String request);
}