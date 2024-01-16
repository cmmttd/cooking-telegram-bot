package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.SPIN_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.START_TOKEN;

@Getter
public enum SpinPickRecipeButtons implements CallbackButton {

    SPIN_PICK_RECIPE_BACK(BACK_TOKEN),
    SPIN_PICK_RECIPE_SPIN(SPIN_TOKEN),
    SPIN_PICK_RECIPE_START(START_TOKEN);

    private final StringToken textToken;

    SpinPickRecipeButtons(StringToken string) {
        this.textToken = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}