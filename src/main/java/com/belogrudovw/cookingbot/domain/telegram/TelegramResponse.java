package com.belogrudovw.cookingbot.domain.telegram;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramResponse(
        @NotNull
        @Pattern(regexp = "^true$|^false$", message = "OK must be true or false")
        String ok,

        @NotNull
        @Valid
        TelegramResult result) {
}