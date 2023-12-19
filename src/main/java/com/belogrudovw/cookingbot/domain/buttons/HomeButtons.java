package com.belogrudovw.cookingbot.domain.buttons;

import lombok.Getter;

@Getter
public enum HomeButtons implements CallbackButton {

    HOME_REQUEST_NEW("Request new"),
    HOME_EXISTS("Pick exists"),
    HOME_HISTORY("History"),
    HOME_BACK("Back");

    private final String text;

    HomeButtons(String string) {
        this.text = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}