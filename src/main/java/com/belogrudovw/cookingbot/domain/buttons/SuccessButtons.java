package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.SUCCESS_BUTTON_TOKEN;

@Getter
public enum SuccessButtons implements CallbackButton {

    SUCCESS(SUCCESS_BUTTON_TOKEN);

    private final StringToken textToken;

    SuccessButtons(StringToken string) {
        this.textToken = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}