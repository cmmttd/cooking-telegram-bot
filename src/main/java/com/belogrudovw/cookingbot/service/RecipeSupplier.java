package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;

import java.util.Collections;
import java.util.UUID;

import reactor.core.publisher.Mono;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.CONTACT_SUPPORT_TOKEN;

public interface RecipeSupplier {
    // TODO: 14/01/2024 Replace RequestProperties by Map
    Mono<Recipe> getRecipe(RequestPreferences request, String additionalQuery);

    default Recipe getStubRecipe(Languages lang) {
        return Recipe.builder()
                .id(UUID.randomUUID())
                .title("Stub recipe")
                .shortDescription(CONTACT_SUPPORT_TOKEN.in(lang))
                .language(lang)
                .ingredients(Collections.emptyList())
                .properties(new Recipe.RecipeProperties(Lightness.LIGHT, MeasurementUnits.METRIC, 15))
                .steps(Collections.emptyList())
                .build();
    }
}