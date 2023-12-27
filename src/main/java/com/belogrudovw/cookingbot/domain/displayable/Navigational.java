package com.belogrudovw.cookingbot.domain.displayable;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Navigational implements Displayable {
    CANCEL("Cancel progress"),
    NEXT("Next step"),
    BACK("Back");

    @JsonValue
    private final String text;

    Navigational(String string) {
        this.text = string;
    }
}
