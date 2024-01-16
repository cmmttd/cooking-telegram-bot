package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Difficulties;
import com.belogrudovw.cookingbot.lexic.StringToken;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;

@Getter
public enum DifficultyButtons implements CallbackButton {
    SETUP_DIFFICULTY_15(Difficulties.MINUTES_15),
    SETUP_DIFFICULTY_30(Difficulties.MINUTES_30),
    SETUP_DIFFICULTY_60(Difficulties.MINUTES_60),
    SETUP_DIFFICULTY_INFINITY(Difficulties.MINUTES_INFINITY),
    SETUP_DIFFICULTY_BACK(BACK_TOKEN);

    private final StringToken textToken;
    private final Difficulties difficulties;

    DifficultyButtons(Difficulties difficulties) {
        this.textToken = difficulties.getDisplayable();
        this.difficulties = difficulties;
    }

    DifficultyButtons(StringToken stringToken) {
        this.difficulties = null;
        this.textToken = stringToken;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }

    @JsonValue
    private String jsonValue() {
        return String.valueOf(difficulties.getMinutes());
    }
}