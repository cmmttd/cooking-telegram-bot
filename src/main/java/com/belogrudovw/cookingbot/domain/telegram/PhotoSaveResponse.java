package com.belogrudovw.cookingbot.domain.telegram;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PhotoSaveResponse(
        @NotNull
        @Pattern(regexp = "^true$|^false$", message = "OK must be true or false")
        String ok,
        @NotNull
        @Valid
        Result callbackData) {

    public record Result(
            @NotNull
            Long id,
            @NotNull
            @Valid
            From from,
            @Valid
            @NotNull
            List<TelegramPhotoSize> photo) {
    }

    public record TelegramPhotoSize(
            @NotNull
            String fileId){
    }
}