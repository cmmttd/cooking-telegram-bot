package com.belogrudovw.cookingbot.domain.telegram;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramPhotoSize(
        @NotNull
        @JsonProperty("file_id")
        String fileId) {
}
