package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.properties.Displayable;
import com.belogrudovw.cookingbot.domain.properties.Languages;

import lombok.Getter;

@Getter
public enum LanguageButtons implements CallbackButton {

    SETUP_LANG_DE(Languages.DE),
    SETUP_LANG_RU(Languages.RU),
    SETUP_LANG_FR(Languages.FR),
    SETUP_LANG_CH(Languages.CH),
    SETUP_LANG_EN(Languages.EN);

    private final String text;

    LanguageButtons(Displayable displayable) {
        this.text = displayable.getIcon();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}