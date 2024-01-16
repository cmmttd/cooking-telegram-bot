package com.belogrudovw.cookingbot.domain.displayable;

import com.belogrudovw.cookingbot.lexic.StringToken;

import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.ANY_BUTTON_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.HEAVY_BUTTON_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.LIGHT_BUTTON_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.MODERATE_BUTTON_TOKEN;

@Getter
public enum Lightness implements Displayable {
    LIGHT(LIGHT_BUTTON_TOKEN),
    MODERATE(MODERATE_BUTTON_TOKEN),
    HEAVY(HEAVY_BUTTON_TOKEN),
    ANY(ANY_BUTTON_TOKEN);

    private final StringToken displayable;

    Lightness(StringToken stringToken) {
        this.displayable = stringToken;
    }

    @JsonCreator
    public static Lightness from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(string.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElse(ANY);
    }
}