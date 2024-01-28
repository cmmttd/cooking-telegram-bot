package com.belogrudovw.cookingbot.domain.telegram;

import java.util.Optional;
import jakarta.validation.constraints.NotNull;

public record TelegramChat(
        @NotNull
        Long id,
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<String> username) {
}
