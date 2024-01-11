package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.RecipeSupplier;
import com.belogrudovw.cookingbot.storage.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeServiceImpl implements RecipeService {

    Storage<UUID, Recipe> recipeStorage;
    RecipeSupplier recipeSupplier;

    // TODO: 15/12/2023 Remove init
    @PostConstruct
    void init() {
        try (Stream<Path> paths = Files.walk(Paths.get("src/main/resources/default_recipes"))) {
            ObjectMapper om = new ObjectMapper();
            om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            Iterator<Path> iterator = paths.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (Files.isRegularFile(path)) {
                    String readString = Files.readString(path);
                    Recipe recipe = om.readValue(readString, Recipe.class);
                    recipeStorage.save(recipe);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Recipe> getRandom(Chat chat) {
        return recipeStorage.all()
                .filter(recipe -> chat.getCurrentRecipe() == null || !recipe.equals(chat.getCurrentRecipe()))
                .filter(recipe -> !chat.getHistory().contains(recipe))
                // TODO: 07/01/2024 Issue:#9 Replace lang filtering by requesting required lang from recipe
                .filter(recipe -> chat.getRequestProperties().getLanguage() == recipe.getLanguage())
                .filter(recipe -> chat.getRequestProperties().matchesTo(recipe.getProperties()))
                .findFirst()
                .map(Mono::just)
                .orElseGet(() -> requestNew(chat))
                .doOnSuccess(recipe -> log.info("Random recipe: {}", recipe.getTitle()));
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        return recipeStorage.get(id);
    }

    @Override
    public Mono<Recipe> requestNew(Chat chat) {
        return recipeSupplier.get(chat.getRequestProperties());
    }
}