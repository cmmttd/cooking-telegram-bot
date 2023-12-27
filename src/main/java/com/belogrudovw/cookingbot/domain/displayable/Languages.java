package com.belogrudovw.cookingbot.domain.displayable;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Languages implements Displayable {
    DE("Deutsch", "🇩🇪"),
    FR("French", "🇫🇷"),
    CH("Chinese", "🇨🇳"),
    RU("Russian", "🇷🇺"),
    UA("Ukrainian", "🇺🇦"),
    EN("English", "🇬🇧/🇺🇸");

    @JsonValue
    private final String text;
    private final String icon;

    Languages(String string, String icon) {
        this.text = string;
        this.icon = icon;
    }

    public static Languages from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(string) || value.getIcon().equals(string))
                .findFirst()
                .orElse(EN);
    }
}