package com.belogrudovw.cookingbot.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IllegalChatStateException extends RuntimeException {

    long chatId;

    public IllegalChatStateException(long chatId, String message) {
        super(message);
        this.chatId = chatId;
    }
}