package com.belogrudovw.cookingbot.domain.telegram;

import java.util.Optional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record Message(
        @NotNull
        Integer messageId,

        @NotNull
        @Valid
        From from,
        @NotNull
        @Valid
        TelegramChat chat,
        String text,
        Optional<Keyboard> replyMarkup) {
}
