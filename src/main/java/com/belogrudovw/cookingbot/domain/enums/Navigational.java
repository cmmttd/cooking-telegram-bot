package com.belogrudovw.cookingbot.domain.enums;

import com.belogrudovw.cookingbot.domain.properties.Displayable;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Navigational implements Displayable {
    CANCEL("Cancel"),
    NEXT("Next"),
    BACK("Back");

    @JsonValue
    private final String text;

    Navigational(String string) {
        this.text = string;
    }
}
