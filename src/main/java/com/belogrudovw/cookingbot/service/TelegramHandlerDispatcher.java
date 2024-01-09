package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.telegram.UserAction;

public interface TelegramHandlerDispatcher {
    void dispatch(UserAction action);
}