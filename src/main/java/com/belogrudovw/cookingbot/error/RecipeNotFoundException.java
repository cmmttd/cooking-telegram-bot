package com.belogrudovw.cookingbot.error;

public class RecipeNotFoundException extends IllegalChatStateException {

    public RecipeNotFoundException(long chatId, String message) {
        super(chatId, message);
    }
}