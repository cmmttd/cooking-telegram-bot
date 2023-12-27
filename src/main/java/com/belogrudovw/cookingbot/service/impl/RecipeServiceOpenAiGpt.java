package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Property;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeServiceOpenAiGpt implements RecipeService {

    static final Recipe STUB_RECIPE = Recipe.builder()
            .id(UUID.randomUUID())
            .title("Stub recipe")
            .shortDescription("Something goes wrong. Please notify my author - @belogrudovw")
            .property(Property.builder().build())
            .steps(List.of(new Recipe.CookingStep(0, "Please contact support:", "@belogrudovw")))
            .build();

    Storage<UUID, Recipe> recipeStorage;

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
    public Recipe getRandom(Chat chat) {
        return recipeStorage.all()
                .filter(recipe -> chat.getCurrentRecipe() == null || !recipe.equals(chat.getCurrentRecipe()))
                .filter(recipe -> !chat.getHistory().contains(recipe))
                .filter(recipe -> recipe.getProperty().matchesTo(chat.getProperty()))
                .findFirst()
                .orElseGet(() -> requestNew(chat));
    }

    @Override
    public Recipe findById(UUID id) {
        return recipeStorage.get(id)
                // TODO: 29/12/2023 Throw RecipeNotFoundException instead
                .orElseGet(this::getStubRecipe);
    }

    @Override
    public Recipe requestNew(Chat chat) {
        // TODO: 18/12/2023 Call gpt instead
        log.debug("Have to call gpt");
        // TODO: 29/12/2023 Remove stub once the api has been implemented
        return getStubRecipe();
    }

    private Recipe getStubRecipe() {
        return STUB_RECIPE;
    }
}