package com.belogrudovw.cookingbot.domain.screen;

import com.belogrudovw.cookingbot.domain.buttons.*;

import lombok.Getter;

@Getter
public enum DefaultScreen implements Screen {

    SETUP_LANG(LanguageButtons.values(), "Choose the recipe's language"),
    SETUP_UNITS(MeasurementUnitButtons.values(), "Choose the measurements unit format"),
    SETUP_LIGHTNESS(LightnessButtons.values(), "Pick lightness of the desired dish"),
    SETUP_CUISINE(CuisineButtons.values(), "Pick one of cuisine"),
    SETUP_DIFFICULTIES(DifficultyButtons.values(), "How long do you plan to cook?"),
    HOME(HomeButtons.values(), "Pick the option"),
    SPIN_PICK_RECIPE(SpinPickRecipeButtons.values(), ""),
    COOKING(CookingButtons.values(), "");

    private final CallbackButton[] buttons;
    private final String text;

    DefaultScreen(CallbackButton[] values, String defaultText) {
        this.buttons = values;
        this.text = defaultText;
    }
}