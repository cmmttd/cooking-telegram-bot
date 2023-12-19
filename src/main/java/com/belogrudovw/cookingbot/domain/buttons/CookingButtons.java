package com.belogrudovw.cookingbot.domain.buttons;

import lombok.Getter;

@Getter
public enum CookingButtons implements CallbackButton {

    COOKING_NEXT("Next step"),
    COOKING_CANCEL("Cancel progress");

    private final String text;

    CookingButtons(String string) {
        this.text = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}