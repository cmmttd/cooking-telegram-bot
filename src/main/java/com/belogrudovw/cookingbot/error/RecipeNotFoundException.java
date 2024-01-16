package com.belogrudovw.cookingbot.error;

import com.belogrudovw.cookingbot.domain.Chat;

public class RecipeNotFoundException extends IllegalChatStateException {

    public RecipeNotFoundException(Chat chat, String message) {
        super(chat, message);
    }
}