package com.belogrudovw.cookingbot.telegram.domain;

public record Button(String text, String callbackData) {
    @Override
    public String toString() {
        return "{\"text\":\"%s\",\"callback_data\":\"%s\"}".formatted(text, callbackData);
    }
}