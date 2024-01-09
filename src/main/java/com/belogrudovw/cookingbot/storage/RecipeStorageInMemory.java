package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Recipe;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
public class RecipeStorageInMemory implements Storage<UUID, Recipe> {

    private final Map<UUID, Recipe> recipes = new ConcurrentHashMap<>();

    @Override
    public void save(@Valid Recipe recipe) {
        if (recipe.getId() == null) {
            recipe.setId(UUID.randomUUID());
        }
        recipes.putIfAbsent(recipe.getId(), recipe);
    }

    @Override
    public Optional<Recipe> get(UUID id) {
        return Optional.ofNullable(recipes.get(id));
    }

    @Override
    public boolean contains(UUID id) {
        return recipes.containsKey(id);
    }

    @Override
    public Stream<Recipe> all() {
        return recipes.values().stream();
    }
}