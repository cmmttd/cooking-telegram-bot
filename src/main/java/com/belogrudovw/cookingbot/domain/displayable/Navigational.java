package com.belogrudovw.cookingbot.domain.displayable;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Navigational implements Displayable {
    NEXT("⏩"),
    CANCEL("⏹️"),
    BACK("↩️");

    @JsonValue
    private final String text;

    Navigational(String string) {
        this.text = string;
    }
}
