package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Recipe;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecipeRepositoryMongo extends MongoRepository<Recipe, UUID> {
}