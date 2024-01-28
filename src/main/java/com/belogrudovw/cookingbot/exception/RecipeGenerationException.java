package com.belogrudovw.cookingbot.exception;

public class RecipeGenerationException extends RuntimeException {
    public RecipeGenerationException(String message) {
        super(message);
    }

    public RecipeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}