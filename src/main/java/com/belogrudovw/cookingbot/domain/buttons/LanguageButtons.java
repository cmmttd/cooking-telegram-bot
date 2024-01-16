package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

@Getter
public enum LanguageButtons implements CallbackButton {

    SETUP_LANG_CH(Languages.CH),
    SETUP_LANG_DE(Languages.DE),
    SETUP_LANG_EN(Languages.EN),
    SETUP_LANG_FR(Languages.FR),
    //    SETUP_LANG_IT(Languages.IT),
//    SETUP_LANG_LV(Languages.LV),
//    SETUP_LANG_JP(Languages.JP),
//    SETUP_LANG_SP(Languages.SP),
    SETUP_LANG_RU(Languages.RU),
    SETUP_LANG_UA(Languages.UA)
//    SETUP_LANG_RS(Languages.RS),
    ;

    private final Languages language;

    LanguageButtons(Languages language) {
        this.language = language;
    }

    @Override
    public StringToken getTextToken() {
        return language.getDisplayable();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}