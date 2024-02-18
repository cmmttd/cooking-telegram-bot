package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.telegram.Keyboard;
import com.belogrudovw.cookingbot.domain.telegram.TelegramResponse;

import reactor.core.publisher.Mono;

public interface ResponseService {
    Mono<TelegramResponse> sendMessage(long chatId, String text, Keyboard keyboard);

    Mono<TelegramResponse> editMessage(long chatId, long messageId, String text, Keyboard keyboard);

    Mono<TelegramResponse> saveImage(byte[] file, String description);

    Mono<TelegramResponse> sendImage(long chatId, String text, Keyboard keyboard, String path);

    Mono<TelegramResponse> editImage(long chatId, long messageId, String text, Keyboard keyboard, String path);
}