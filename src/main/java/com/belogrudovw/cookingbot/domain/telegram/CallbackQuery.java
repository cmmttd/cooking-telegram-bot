package com.belogrudovw.cookingbot.domain.telegram;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CallbackQuery(
        @NotNull
        Long id,
        @NotNull
        @Valid
        From from,
        @NotNull
        @Valid
        Message message,
        String data) {
}
