package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.REQUEST_CUSTOM_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.REQUEST_HISTORY_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.REQUEST_RANDOM_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.REQUEST_RESET_PREFERENCES_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;

@Getter
public enum HomeButtons implements CallbackButton {

    HOME_CUSTOM(REQUEST_CUSTOM_TOKEN),
    HOME_RANDOM(REQUEST_RANDOM_TOKEN),
    HOME_HISTORY(REQUEST_HISTORY_TOKEN),
    HOME_RESET_PREFERENCES(REQUEST_RESET_PREFERENCES_TOKEN),
    HOME_BACK(BACK_TOKEN);

    private final StringToken textToken;

    HomeButtons(StringToken stringToken) {
        this.textToken = stringToken;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}