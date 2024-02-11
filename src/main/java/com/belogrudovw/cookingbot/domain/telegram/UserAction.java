package com.belogrudovw.cookingbot.domain.telegram;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
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
                .orElseGet(() -> -1 * ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE - 1));
    }

    public String getUserName() {
        return message()
                .or(() -> callbackQuery()
                        .map(CallbackQuery::message))
                .map(Message::chat)
                .map(chat -> Stream.of(chat.firstName(), chat.lastName(), chat.username())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(namesPart -> !namesPart.isBlank())
                        .collect(Collectors.joining(" ")))
                .orElse("User name unknown");
    }
}