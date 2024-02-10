package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.config.RecoveryProperties;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.util.FilesUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "storage", name = "source", havingValue = "mongo")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecipeStorageMongo implements Storage<UUID, Recipe> {

    RecipeRepositoryMongo recipeRepositoryMongo;
    RecoveryProperties recoveryProperties;

    @PostConstruct
    void recover() {
        if (recoveryProperties.isNeeded() && recoveryProperties.defaultRecipesPath() != null) {
            log.info("Recipe recovery starts from the folder: {}", recoveryProperties.defaultRecipesPath());
            int recovered = FilesUtil.recover(recoveryProperties.defaultRecipesPath(), Recipe.class, this::save);
            log.info("Recipe recovery completed successfully for: {}", recovered);
        }
    }

    @Override
    public void save(Recipe recipe) {
        if (recipe.getId() == null) {
            recipe.setId(UUID.randomUUID());
        }
        recipeRepositoryMongo.save(recipe);
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        return recipeRepositoryMongo.findById(id);
    }

    @Override
    public List<Recipe> findByIds(Collection<UUID> ids) {
        return recipeRepositoryMongo.findAllById(ids);
    }

    @Override
    public boolean contains(UUID id) {
        return recipeRepositoryMongo.existsById(id);
    }

    @Override
    public Stream<Recipe> all() {
        return recipeRepositoryMongo.findAll().stream();
    }

    @Override
    public long size() {
        return recipeRepositoryMongo.count();
    }
}