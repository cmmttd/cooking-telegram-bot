package com.belogrudovw.cookingbot.domain.buttons;

import lombok.Getter;

@Getter
public enum SpinPickRecipeButtons implements CallbackButton {

    SPIN_PICK_RECIPE_START("Start timer"),
    SPIN_PICK_RECIPE_SPIN("Spin the recipe"),
    SPIN_PICK_RECIPE_BACK("Back");

    private final String text;

    SpinPickRecipeButtons(String string) {
        this.text = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}