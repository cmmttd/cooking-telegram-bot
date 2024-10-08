package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;

import reactor.core.publisher.Mono;

public interface RecipeService {
    Mono<Recipe> getRandom(Chat chat);

    Mono<Recipe> requestNew(Chat chat);
}