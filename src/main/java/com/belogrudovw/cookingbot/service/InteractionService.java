package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.screen.Screen;

import reactor.core.publisher.Mono;

public interface InteractionService {
    void showSpinner(Chat chat, int messageId);

    void showResponse(Chat chat, Screen nextScreen);

    void showResponse(Chat chat, long messageId, Screen nextScreen);

    void showResponse(Chat chat, Screen nextScreen, String imageId);

    void showResponse(Chat chat, long messageId, Screen nextScreen, String imageId);

    Mono<String> saveImage(byte[] file, String description);
}