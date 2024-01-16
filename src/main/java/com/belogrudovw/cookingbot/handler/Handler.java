package com.belogrudovw.cookingbot.handler;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

public interface Handler {
    void handle(UserAction action);

    default void handle(Chat chat) {
        throw new UnsupportedOperationException("Not implemented for " + this.getClass());
    }
}
