package com.belogrudovw.cookingbot.domain.displayable;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum Difficulties implements Displayable {
    MINUTES_15("15 minutes", 15),
    MINUTES_30("30 minutes", 30),
    MINUTES_60("60 minutes", 60),
    MINUTES_INFINITY("Doesn't matter", Integer.MAX_VALUE);

    private final String text;
    private final int minutes;

    Difficulties(String string, int minutes) {
        this.text = string;
        this.minutes = minutes;
    }

    public static Difficulties from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equalsIgnoreCase(string))
                .findFirst()
                .orElse(MINUTES_INFINITY);
    }
}