package com.belogrudovw.cookingbot.exception;

public class TranslationGenerationException extends RuntimeException {
    public TranslationGenerationException(String message) {
        super(message);
    }

    public TranslationGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}