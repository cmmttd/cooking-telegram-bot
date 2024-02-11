package com.belogrudovw.cookingbot.domain.telegram;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramResult(
        @JsonAlias({"message_id", "id"})
        @NotNull
        Integer messageId,

        From from,

        TelegramChat chat,

        long date,

        @Valid
        List<TelegramPhotoSize> photo) {
}