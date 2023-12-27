package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Displayable;
import com.belogrudovw.cookingbot.domain.displayable.Navigational;
import com.belogrudovw.cookingbot.domain.displayable.Difficulties;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DifficultyButtons implements CallbackButton {
    SETUP_DIFFICULTY_15(Difficulties.MINUTES_15),
    SETUP_DIFFICULTY_30(Difficulties.MINUTES_30),
    SETUP_DIFFICULTY_60(Difficulties.MINUTES_60),
    SETUP_DIFFICULTY_INFINITY(Difficulties.MINUTES_INFINITY),
    SETUP_DIFFICULTY_BACK(Navigational.BACK);

    @JsonValue
    private final String text;

    DifficultyButtons(Displayable displayable) {
        this.text = displayable.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}