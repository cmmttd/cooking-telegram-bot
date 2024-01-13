package com.belogrudovw.cookingbot.domain.displayable;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Languages implements Displayable {
    DE("Deutsch", " 🇩🇪 "),
    FR("French", " 🇫🇷 "),
    CH("Chinese", " 🇨🇳 "),
    IT("Italian", " 🇮🇹 "),
    SP("Spanish", " 🇪🇸 "),
    LV("Lithuanian", " 🇱🇹 "),
    RU("Russian", " 🇷🇺  "),
    RS("Serbian", " 🇷🇸 "),
    UA("Ukrainian", " 🇺🇦 "),
    JP("Japan", " 🇯🇵 "),
    EN("English", "🇬🇧/🇺🇸");

    private final String text;
    private final String icon;

    Languages(String string, String icon) {
        this.text = string;
        this.icon = icon;
    }

    @JsonCreator
    public static Languages from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equalsIgnoreCase(string)
                        || value.getIcon().equals(string)
                        || value.name().equalsIgnoreCase(string))
                .findFirst()
                .orElse(EN);
    }
}