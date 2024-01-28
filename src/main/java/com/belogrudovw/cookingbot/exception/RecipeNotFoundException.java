package com.belogrudovw.cookingbot.exception;

import com.belogrudovw.cookingbot.domain.Chat;

public class RecipeNotFoundException extends IllegalChatStateException {

    public RecipeNotFoundException(Chat chat, String message) {
        super(chat, message);
    }
}