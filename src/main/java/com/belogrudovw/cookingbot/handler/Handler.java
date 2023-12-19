package com.belogrudovw.cookingbot.handler;

import com.belogrudovw.cookingbot.telegram.domain.UserAction;

public interface Handler {
    void handle(UserAction action);
}
