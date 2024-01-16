package com.belogrudovw.cookingbot.domain.displayable;

import com.belogrudovw.cookingbot.lexic.StringToken;

import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.IMPERIAL_BUTTON_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.METRIC_BUTTON_TOKEN;

@Getter
public enum MeasurementUnits implements Displayable {
    METRIC(METRIC_BUTTON_TOKEN),
    IMPERIAL(IMPERIAL_BUTTON_TOKEN);

    private final StringToken displayable;

    MeasurementUnits(StringToken stringToken) {
        this.displayable = stringToken;
    }

    @JsonCreator
    public static MeasurementUnits from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(string.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElse(METRIC);
    }
}