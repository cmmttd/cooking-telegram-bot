package com.belogrudovw.cookingbot.domain.telegram;

import java.util.Optional;
import jakarta.validation.constraints.NotNull;

public record From(
        @NotNull
        Long id,
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<String> username,
        String languageCode) {
}
