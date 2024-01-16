package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;

@Getter
public enum LightnessButtons implements CallbackButton {

    SETUP_LIGHTNESS_LIGHT(Lightness.LIGHT),
    SETUP_LIGHTNESS_MODERATE(Lightness.MODERATE),
    SETUP_LIGHTNESS_HEAVY(Lightness.HEAVY),
    SETUP_LIGHTNESS_ANY(Lightness.ANY),
    SETUP_LIGHTNESS_BACK(BACK_TOKEN);

    private final StringToken textToken;
    private final Lightness lightness;

    LightnessButtons(Lightness lightness) {
        this.lightness = lightness;
        this.textToken = lightness.getDisplayable();
    }

    LightnessButtons(StringToken stringToken) {
        this.lightness = null;
        this.textToken = stringToken;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}
