package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.SHOW_CALORIC_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.SHOW_IMAGE_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.SPIN_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.START_TOKEN;

@Getter
public enum RecipeButtons implements CallbackButton {

//    RECIPE_CALORIC(SHOW_CALORIC_TOKEN),
//    RECIPE_IMAGE(SHOW_IMAGE_TOKEN),
    RECIPE_BACK(BACK_TOKEN),
    RECIPE_SPIN(SPIN_TOKEN),
    RECIPE_START(START_TOKEN);

    private final StringToken textToken;

    RecipeButtons(StringToken string) {
        this.textToken = string;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}