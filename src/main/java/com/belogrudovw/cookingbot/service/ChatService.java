package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;

import java.util.Optional;

public interface ChatService {

    boolean isExists(long chatId);

    Chat findById(long chatId);

    void save(Chat chat);

    Optional<Recipe.CookingStep> nextRecipeStep(Chat chatId);

    void setNewRecipe(Chat chat, Recipe recipe);
}