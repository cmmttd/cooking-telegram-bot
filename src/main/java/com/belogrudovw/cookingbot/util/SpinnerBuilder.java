package com.belogrudovw.cookingbot.util;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.lexic.MultilingualTokens;

import lombok.experimental.UtilityClass;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.ADDITIONAL_PARAMETERS_TOKEN;

@UtilityClass
public final class SpinnerBuilder {

    public static String buildAwaitSpinner(Chat chat) {
        RequestPreferences requestPreferences = chat.getRequestPreferences();
        Languages language = requestPreferences.getLanguage();
        String spinnerString = MultilingualTokens.AWAIT_TOKEN.in(language)
                .formatted(
                        language.getDisplayable().in(language).strip(),
                        requestPreferences.getLightness().getDisplayable().in(language).strip(),
                        requestPreferences.getDifficulty().getDisplayable().in(language).strip(),
                        requestPreferences.getUnits().getDisplayable().in(language).strip()
                );
        if (chat.isAwaitCustomQuery() && chat.getAdditionalQuery() != null) {
            spinnerString += ADDITIONAL_PARAMETERS_TOKEN.in(language) + chat.getAdditionalQuery();
        }
        return spinnerString;
    }
}