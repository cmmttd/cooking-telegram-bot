package com.belogrudovw.cookingbot.error;

import com.belogrudovw.cookingbot.domain.Chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IllegalChatStateException extends RuntimeException {

    transient Chat chat;

    public IllegalChatStateException(Chat chat, String message) {
        super(message);
        this.chat = chat;
    }
}