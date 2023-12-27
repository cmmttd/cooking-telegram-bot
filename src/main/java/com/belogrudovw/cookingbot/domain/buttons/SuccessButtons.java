package com.belogrudovw.cookingbot.domain.buttons;

import lombok.Getter;

@Getter
public enum SuccessButtons implements CallbackButton {

    SUCCESS("💕");

    private final String text;

    SuccessButtons(String string) {
        this.text = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}