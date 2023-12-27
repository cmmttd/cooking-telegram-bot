package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Displayable;
import com.belogrudovw.cookingbot.domain.displayable.Navigational;

import lombok.Getter;

@Getter
public enum HomeButtons implements CallbackButton {

    HOME_REQUEST_NEW("Request new"),
    HOME_EXISTS("Pick exists"),
    HOME_HISTORY("History"),
    HOME_BACK(Navigational.BACK);

    private final String text;

    HomeButtons(String string) {
        this.text = string;
    }

    HomeButtons(Displayable displayable) {
        this.text = displayable.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}