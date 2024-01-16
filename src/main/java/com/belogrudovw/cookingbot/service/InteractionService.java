package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.screen.Screen;

public interface InteractionService {
    void showSpinner(Chat chat);

    void showSpinner(Chat chat, int messageId);

    void showResponse(Chat chat, Screen nextScreen);

    void showResponse(Chat chat, long messageId, Screen nextScreen);
}