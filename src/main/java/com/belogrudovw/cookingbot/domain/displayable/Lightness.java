package com.belogrudovw.cookingbot.domain.displayable;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Lightness implements Displayable {
    LIGHT("Light"),
    MODERATE("Moderate"),
    HEAVY("Heavy"),
    ANY("Any");

    @JsonValue
    private final String text;

    Lightness(String string) {
        this.text = string;
    }


    public static Lightness from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(string))
                .findFirst()
                .orElse(ANY);
    }
}