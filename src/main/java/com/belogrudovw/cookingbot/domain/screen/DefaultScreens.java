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
import com.belogrudovw.cookingbot.lexic.MultilingualTokens;
import com.belogrudovw.cookingbot.lexic.SingleValueTokens;
import com.belogrudovw.cookingbot.lexic.StringToken;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum DefaultScreens implements Screen {

    SETUP_LANG(LanguageButtons.values(), MultilingualTokens.CHOOSE_LANG_TOKEN),
    SETUP_UNITS(MeasurementUnitButtons.values(), MultilingualTokens.CHOOSE_MEASUREMENTS_TOKEN),
    SETUP_LIGHTNESS(LightnessButtons.values(), MultilingualTokens.CHOOSE_LIGHTNESS_TOKEN),
    SETUP_DIFFICULTIES(DifficultyButtons.values(), MultilingualTokens.CHOOSE_DIFFICULTIES_TOKEN),
    HOME(HomeButtons.values(), MultilingualTokens.HOW_TO_COOK_TOKEN),
    SPIN_PICK_RECIPE(SpinPickRecipeButtons.values(), SingleValueTokens.EMPTY_TOKEN),
    COOKING(CookingButtons.values(), SingleValueTokens.EMPTY_TOKEN),
    SUCCESS(SuccessButtons.values(), MultilingualTokens.CONGRATULATIONS_TOKEN);

    private final List<CallbackButton> buttons;
    private final StringToken titleText;

    DefaultScreens(CallbackButton[] buttons, StringToken titleText) {
        this.buttons = Arrays.stream(buttons).toList();
        this.titleText = titleText;
    }

    @Override
    public StringToken getTextToken() {
        return titleText;
    }
}