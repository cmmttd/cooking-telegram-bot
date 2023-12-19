package com.belogrudovw.cookingbot.domain.properties;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Cuisines implements Displayable {
    ANY("Any"),
    CH("Chinese"),
    GB("British"),
    IN("Indian"),
    IR("Irish"),
    RU("Russian"),
    IT("Italian");

    @JsonValue
    private final String text;

    Cuisines(String string) {
        this.text = string;
    }


    public static Cuisines from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(string))
                .findFirst()
                .orElse(IT);
    }
}