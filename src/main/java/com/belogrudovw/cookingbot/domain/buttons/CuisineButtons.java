package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.properties.Cuisines;
import com.belogrudovw.cookingbot.domain.properties.Displayable;
import com.belogrudovw.cookingbot.domain.enums.Navigational;

import lombok.Getter;

@Getter
public enum CuisineButtons implements CallbackButton {

    SETUP_CUISINE_IT(Cuisines.IT),
    SETUP_CUISINE_CH(Cuisines.CH),
    SETUP_CUISINE_ANY(Cuisines.ANY),
    SETUP_CUISINE_BACK(Navigational.BACK);

    private final String text;

    CuisineButtons(Displayable enumValue) {
        this.text = enumValue.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}
