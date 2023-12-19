package com.belogrudovw.cookingbot.telegram.domain;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;

@Builder
public record Keyboard(List<List<Button>> inlineKeyboard) {
    @Override
    public String toString() {
        String buttonsString = inlineKeyboard.stream()
                .map(row -> row.stream()
                        .map(Button::toString)
                        .collect(Collectors.joining(",", "[", "]")))
                .collect(Collectors.joining(",", "[", "]"));
        return "{\"inline_keyboard\":%s}".formatted(buttonsString);
    }
}