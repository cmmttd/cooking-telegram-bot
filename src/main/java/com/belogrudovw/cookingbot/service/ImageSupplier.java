package com.belogrudovw.cookingbot.service;

import reactor.core.publisher.Mono;

public interface ImageSupplier {
    Mono<byte[]> getImageByText(String request);
}