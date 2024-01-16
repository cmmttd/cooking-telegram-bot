package com.belogrudovw.cookingbot.domain.displayable;

import com.belogrudovw.cookingbot.lexic.StringToken;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.ANY_BUTTON_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.MINUTES_15_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.MINUTES_30_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.MINUTES_60_TOKEN;

@Getter
public enum Difficulties implements Displayable {
    MINUTES_15(MINUTES_15_TOKEN, 15),
    MINUTES_30(MINUTES_30_TOKEN, 30),
    MINUTES_60(MINUTES_60_TOKEN, 60),
    MINUTES_INFINITY(ANY_BUTTON_TOKEN, Integer.MAX_VALUE);

    private final StringToken displayable;
    private final int minutes;

    Difficulties(StringToken stringToken, int minutes) {
        this.displayable = stringToken;
        this.minutes = minutes;
    }

    @JsonCreator
    public static Difficulties from(String string) {
        return Arrays.stream(values())
                .filter(value -> string.startsWith(String.valueOf(value.getMinutes())))
                .findFirst()
                .orElse(MINUTES_INFINITY);
    }
}