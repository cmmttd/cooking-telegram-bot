package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Recipe;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryRecipeStorage implements Storage<UUID, Recipe> {

    private static final Map<UUID, Recipe> CACHE = new ConcurrentHashMap<>();

    @Override
    public void save(Recipe recipe) {
        if (recipe.getId() == null) {
            recipe.setId(UUID.randomUUID());
        }
        CACHE.putIfAbsent(recipe.getId(), recipe);
    }

    @Override
    public Optional<Recipe> get(UUID id) {
        return Optional.ofNullable(CACHE.get(id));
    }

    @Override
    public boolean contains(UUID id) {
        return CACHE.containsKey(id);
    }

    @Override
    public Stream<Recipe> all() {
        return CACHE.values().stream();
    }
}