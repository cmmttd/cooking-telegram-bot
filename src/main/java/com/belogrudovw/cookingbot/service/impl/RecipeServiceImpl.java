package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.RecipeSupplier;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.util.FilesUtil;
import com.belogrudovw.cookingbot.util.Pair;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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

    static final String RECIPE_FOLDER_PATH = "src/main/resources/default_recipes/";

    Storage<UUID, Recipe> recipeStorage;
    RecipeSupplier recipeSupplier;

    @PostConstruct
    void recover() {
        FilesUtil.recover(RECIPE_FOLDER_PATH, Recipe.class, recipeStorage::save);
        log.info("Recipe recover succeed for: {}", recipeStorage.all().count());
    }

    @PreDestroy
    void backup() {
        var recipeStream = recipeStorage.all().map(recipe -> new Pair<>(String.valueOf(recipe.getTitle()), recipe));
        FilesUtil.backup(RECIPE_FOLDER_PATH, recipeStream);
        log.info("Recipe data backup succeed for: {}", recipeStorage.all().count());
    }

    @Override
    public Mono<Recipe> getRandom(Chat chat) {
        Stream<Recipe> recipeStream = recipeStorage.all().parallel();
        if (chat.isAwaitCustomQuery()) {
            // TODO: 14/01/2024 Add full-body text search by additional request
            recipeStream = recipeStream.filter(recipe -> {
                String additionalQuery = chat.getAdditionalQuery().toLowerCase(Locale.ROOT);
                return recipe.getShortDescription().toLowerCase(Locale.ROOT).contains(additionalQuery)
                        || recipe.getTitle().toLowerCase(Locale.ROOT).contains(additionalQuery);
            });
        }
        return recipeStream
                .filter(recipe -> !chat.getHistory().contains(recipe))
                .filter(recipe -> chat.getCurrentRecipe() == null || !recipe.equals(chat.getCurrentRecipe()))
                // TODO: 07/01/2024 Issue:#9 Replace lang filtering by requesting required lang from recipe
                .filter(recipe -> chat.getRequestProperties().getLanguage() == recipe.getLanguage())
                .filter(recipe -> chat.getRequestProperties().matchesTo(recipe.getProperties()))
                .findFirst()
                .map(Mono::just)
                .orElseGet(() -> requestNew(chat))
                .doOnSuccess(recipe -> log.info("Random recipe '{}' for chat {}", recipe.getTitle(), chat.getId()));
    }

    @Override
    public Mono<Recipe> requestNew(Chat chat) {
        String additionalQuery = chat.isAwaitCustomQuery() && chat.getAdditionalQuery() != null ? chat.getAdditionalQuery() : "";
        log.info("New recipe requested for chat {} {}", chat.getId(), additionalQuery);
        return recipeSupplier.get(chat.getRequestProperties(), additionalQuery);
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        return recipeStorage.get(id);
    }
}