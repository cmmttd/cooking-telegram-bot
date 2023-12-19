package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Recipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryRecipeStorage implements Storage<Long, Recipe> {

    private static final Map<Long, Recipe> CACHE = new HashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    @Override
    @SneakyThrows
    public void save(Recipe recipe) {
        try {
            if (LOCK.tryLock(10, TimeUnit.SECONDS)) {
                long nextId = CACHE.size() + 1L;
                recipe.setId(nextId);
                CACHE.put(nextId, recipe);
            }
        } catch (InterruptedException e) {
            log.error("Attempt to save recipe failed");
            throw e;
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public Optional<Recipe> get(Long id) {
        return Optional.ofNullable(CACHE.get(id));
    }

    @Override
    public boolean contains(Long id) {
        return CACHE.containsKey(id);
    }

    @Override
    public Stream<Recipe> all() {
        return CACHE.values().stream();
    }
}