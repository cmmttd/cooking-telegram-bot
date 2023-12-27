package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.properties.Property;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    Recipe getRandom(Chat chat);

    Recipe getById(UUID id);

    Recipe requestNew(Property property);

    default Recipe getStubRecipe() {
        List<Recipe.CookingStep> cookingSteps = List.of(new Recipe.CookingStep(0, "nothing", "empty"));
        Property property = Property.builder().build();
        return Recipe.builder()
                .title("Stub recipe")
                .shortDescription("Something goes wrong. Please notify my author - @belogrudovw")
                .property(property)
                .steps(cookingSteps)
                .build();
    }
}
