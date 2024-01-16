package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.CANCEL_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.NEXT_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.PAUSE_TOKEN;

@Getter
public enum CookingButtons implements CallbackButton {

    COOKING_CANCEL(CANCEL_TOKEN),
    COOKING_PAUSE(PAUSE_TOKEN),
    COOKING_NEXT(NEXT_TOKEN);

    private final StringToken textToken;

    CookingButtons(StringToken stringToken) {
        this.textToken = stringToken;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}