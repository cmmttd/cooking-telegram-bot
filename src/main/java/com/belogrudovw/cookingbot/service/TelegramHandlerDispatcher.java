package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.telegram.domain.UserAction;

public interface TelegramHandlerDispatcher {
    void dispatch(UserAction action);
}