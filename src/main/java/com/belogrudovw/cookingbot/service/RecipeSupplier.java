package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface RecipeSupplier {
    // TODO: 14/01/2024 Replace RequestProperties by Map
    Mono<Recipe> get(RequestPreferences request, String additionalQuery);

    default Recipe getStubRecipe() {
        return STUB_RECIPE;
    }

    Recipe STUB_RECIPE = Recipe.builder()
            .id(UUID.randomUUID())
            .title("Stub recipe")
            .shortDescription("Something goes wrong. Please notify my author - @belogrudovw")
            .language(Languages.EN)
            .ingredients(Collections.emptyList())
            .properties(new Recipe.RecipeProperties(Lightness.LIGHT, MeasurementUnits.METRIC, 15))
            .steps(List.of(new Recipe.Step(0, 0, "Please contact support:", "@belogrudovw")))
            .build();
}