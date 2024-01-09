package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.handler.Handler;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

import java.util.Set;
import java.util.stream.Collectors;

public interface CallbackHandler extends Handler {
    Set<String> getSupported();

    void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery);

    default Set<String> setOfCallbackDataFrom(Screen screen) {
        return screen.getButtons().stream()
                .map(CallbackButton::getCallbackData)
                .collect(Collectors.toSet());
    }
}
