package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.telegram.Keyboard;

public interface ResponseService {
    // TODO: 14/01/2024 Return the new message id from response
    void sendMessage(long chatId, String text, Keyboard keyboard);

    void editMessage(long chatId, long messageId, String text, Keyboard keyboard);
}