package com.belogrudovw.cookingbot.domain.screen;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.buttons.DifficultyButtons;
import com.belogrudovw.cookingbot.domain.buttons.HomeButtons;
import com.belogrudovw.cookingbot.domain.buttons.LanguageButtons;
import com.belogrudovw.cookingbot.domain.buttons.LightnessButtons;
import com.belogrudovw.cookingbot.domain.buttons.MeasurementUnitButtons;
import com.belogrudovw.cookingbot.domain.buttons.SpinPickRecipeButtons;
import com.belogrudovw.cookingbot.domain.buttons.SuccessButtons;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum DefaultScreens implements Screen {

    SETUP_LANG(LanguageButtons.class, "Choose the recipe's language"),
    SETUP_LANG2(LanguageButtons.class, "Choose the recipe's language"),
    SETUP_UNITS(MeasurementUnitButtons.class, "Choose the measurements unit format"),
    SETUP_LIGHTNESS(LightnessButtons.class, "Pick lightness of the desired dish"),
    SETUP_DIFFICULTIES(DifficultyButtons.class, "How long do you plan to cook?"),
    HOME(HomeButtons.class, "Pick the option"),
    SPIN_PICK_RECIPE(SpinPickRecipeButtons.class, ""),
    COOKING(CookingButtons.class, ""),
    SUCCESS(SuccessButtons.class, "Congratulations! You're cooking master!\nOne more time?");

    private final List<CallbackButton> buttons;
    private final String text;

    <T extends Enum<T> & CallbackButton> DefaultScreens(Class<T> buttons, String text) {
        this.buttons = Arrays.stream(buttons.getEnumConstants())
                .map(CallbackButton.class::cast)
                .toList();
        this.text = text;
    }
}