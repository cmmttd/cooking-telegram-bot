package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Navigational;
import com.belogrudovw.cookingbot.domain.displayable.Displayable;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;

import lombok.Getter;

@Getter
public enum LightnessButtons implements CallbackButton {

    SETUP_LIGHTNESS_LIGHT(Lightness.LIGHT),
    SETUP_LIGHTNESS_MODERATE(Lightness.MODERATE),
    SETUP_LIGHTNESS_HEAVY(Lightness.HEAVY),
    SETUP_LIGHTNESS_ANY(Lightness.ANY),
    SETUP_LIGHTNESS_BACK(Navigational.BACK);

    private final String text;

    LightnessButtons(Displayable enumValue) {
        this.text = enumValue.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}
