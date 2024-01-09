package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.Displayable;
import com.belogrudovw.cookingbot.domain.displayable.Navigational;

import lombok.Getter;

@Getter
public enum SpinPickRecipeButtons implements CallbackButton {

    SPIN_PICK_RECIPE_BACK(Navigational.BACK),
    SPIN_PICK_RECIPE_SPIN("🔄"),
    SPIN_PICK_RECIPE_START("▶️");

    private final String text;

    SpinPickRecipeButtons(String string) {
        this.text = string;
    }

    SpinPickRecipeButtons(Displayable displayable) {
        this.text = displayable.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}