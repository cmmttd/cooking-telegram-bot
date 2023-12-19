package com.belogrudovw.cookingbot.telegram.domain;

import java.util.Optional;

public record UserAction(int updateId, Optional<Message> message, Optional<CallbackQuery> callbackQuery) {

    public long getChatId() {
        return message()
                .or(() -> callbackQuery()
                        .map(CallbackQuery::message))
                .map(Message::chat)
                .map(TelegramChat::id)
                .orElseThrow();
    }

    public record CallbackQuery(long id, From from, Message message, String data) {
    }

    public record TelegramChat(long id, Optional<String> firstName, Optional<String> lastName, Optional<String> username) {
    }

    public record From(long id, Optional<String> firstName, Optional<String> lastName, Optional<String> username, String languageCode) {
    }

    public record Message(int messageId, From from, TelegramChat chat, String text, Optional<Keyboard> replyMarkup) {
    }
}