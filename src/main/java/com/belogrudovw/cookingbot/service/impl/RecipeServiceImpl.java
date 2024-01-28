package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.RecipeSupplier;
import com.belogrudovw.cookingbot.storage.Storage;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeServiceImpl implements RecipeService {

    final Storage<UUID, Recipe> recipeStorage;
    final RecipeSupplier recipeSupplier;

    @Override
    public Mono<Recipe> getRandom(Chat chat) {
        log.debug("Random recipe requested for chat {} - {}", chat.getId(), chat.getAdditionalQuery());
        Stream<Recipe> recipeStream = recipeStorage.all().parallel();
        if (chat.isAwaitCustomQuery()) {
            // TODO: 14/01/2024 Add full-body text search by additional request
            recipeStream = recipeStream.filter(recipe -> {
                String additionalQuery = chat.getAdditionalQuery().toLowerCase(Locale.ROOT);
                return recipe.getShortDescription().toLowerCase(Locale.ROOT).contains(additionalQuery)
                        || recipe.getTitle().toLowerCase(Locale.ROOT).contains(additionalQuery);
            });
        }
        Set<Recipe> recipeSet = recipeStream
                .filter(recipe -> !chat.getHistory().contains(recipe.getId()))
                .filter(recipe -> !recipe.getId().equals(chat.getCurrentRecipe()))
                // TODO: 07/01/2024 Issue:#9 Replace lang filtering by requesting required lang from recipe
                .filter(recipe -> chat.getRequestPreferences().getLanguage() == recipe.getLanguage())
                .filter(recipe -> chat.getRequestPreferences().matchesTo(recipe.getProperties()))
                .collect(Collectors.toSet());
        return recipeSet.stream()
                .skip(recipeSet.isEmpty() ? 0 : ThreadLocalRandom.current().nextInt(0, recipeSet.size()))
                .findAny()
                .map(Mono::just)
                .orElseGet(() -> {
                    log.warn("Existing recipe hasn't found for chat {} - {}", chat.getId(), chat.getAdditionalQuery());
                    return requestNew(chat);
                })
                .doOnSuccess(recipe -> log.debug("Random recipe '{}' for chat {}", recipe.getTitle(), chat.getId()));
    }

    @Override
    public Mono<Recipe> requestNew(Chat chat) {
        String additionalQuery = chat.isAwaitCustomQuery() && chat.getAdditionalQuery() != null ? chat.getAdditionalQuery() : "";
        log.info("New recipe generation requested for chat {} - {}", chat.getId(), additionalQuery);
        return recipeSupplier.get(chat.getRequestPreferences(), additionalQuery)
                .doOnSuccess(recipe -> log.info("New recipe '{}' has been generated for chat {}", recipe.getTitle(), chat.getId()));
    }
}