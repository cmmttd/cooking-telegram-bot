package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Navigational;
import com.belogrudovw.cookingbot.domain.displayable.Displayable;

import lombok.Getter;

@Getter
public enum CookingButtons implements CallbackButton {

    COOKING_CANCEL(Navigational.CANCEL),
    COOKING_NEXT(Navigational.NEXT);

    private final String text;

    CookingButtons(Displayable displayable) {
        this.text = displayable.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}