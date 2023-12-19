package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.telegram.domain.Keyboard;

public interface ResponseService {
    void sendMessage(long chatId, String text, Keyboard keyboard);

    void editMessage(long chatId, long messageId, String text, Keyboard keyboard);
}