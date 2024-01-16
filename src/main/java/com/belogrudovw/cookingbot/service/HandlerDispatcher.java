package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.telegram.UserAction;

public interface HandlerDispatcher {
    void dispatch(UserAction action);
}