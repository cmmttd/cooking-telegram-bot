package com.belogrudovw.cookingbot.domain.telegram;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.constraints.NotNull;

public record UserAction(@NotNull Integer updateId, Optional<Message> message, Optional<CallbackQuery> callbackQuery) {

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

    public record CallbackQuery(long id, From from, @NotNull Message message, String data) {
    }

    public record TelegramChat(long id, Optional<String> firstName, Optional<String> lastName, Optional<String> username) {
    }

    public record From(long id, Optional<String> firstName, Optional<String> lastName, Optional<String> username, String languageCode) {
    }

    public record Message(@NotNull Integer messageId, From from, TelegramChat chat, String text, Optional<Keyboard> replyMarkup) {
    }
}