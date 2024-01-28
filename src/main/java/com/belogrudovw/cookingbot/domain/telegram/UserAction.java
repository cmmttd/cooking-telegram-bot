package com.belogrudovw.cookingbot.domain.telegram;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserAction(
        @NotNull
        @Positive
        Integer updateId,
        Optional<@Valid Message> message,
        Optional<@Valid CallbackQuery> callbackQuery) {

    public long getChatId() {
        return message()
                .or(() -> callbackQuery()
                        .map(CallbackQuery::message))
                .map(Message::chat)
                .map(TelegramChat::id)
                .orElseThrow();
    }

    public String getUserName() {
        return message()
                .or(() -> callbackQuery()
                        .map(CallbackQuery::message))
                .map(Message::chat)
                .map(chat -> Stream.of(chat.firstName(), chat.lastName(), chat.username())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(x -> !x.isBlank())
                        .collect(Collectors.joining(" ")))
                .orElseThrow();
    }
}