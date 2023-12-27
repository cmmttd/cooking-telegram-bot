package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;

import java.util.UUID;

public interface RecipeService {
    Recipe getRandom(Chat chat);

    Recipe requestNew(Chat chat);

    Recipe findById(UUID id);
}
