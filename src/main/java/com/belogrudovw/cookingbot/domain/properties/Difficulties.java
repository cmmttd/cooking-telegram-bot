package com.belogrudovw.cookingbot.domain.properties;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Difficulties implements Displayable {
    MINUTES_15("15 minutes"),
    MINUTES_30("30 minutes"),
    MINUTES_60("60 minutes"),
    MINUTES_INFINITY("Doesn't matter");

    @JsonValue
    private final String text;

    Difficulties(String text) {
        this.text = text;
    }


    public static Difficulties from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(string))
                .findFirst()
                .orElse(MINUTES_60);
    }
}