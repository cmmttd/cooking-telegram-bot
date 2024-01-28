package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.util.FilesUtil;
import com.belogrudovw.cookingbot.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecipeStorageInMemory implements Storage<UUID, Recipe> {

    private final Map<UUID, Recipe> recipes = new ConcurrentHashMap<>();

    @Value("${recovery.default-recipes-path}")
    private String defaultRecipeFolderPath;

    @PostConstruct
    void recover() {
        if (defaultRecipeFolderPath != null) {
            log.info("Recipe recovery starts from the folder: {}", defaultRecipeFolderPath);
            FilesUtil.recover(defaultRecipeFolderPath, Recipe.class, this::save);
            log.info("Recipe recovery completed successfully for: {}", this.all().count());
        }
    }

    @PreDestroy
    void backup() {
        if (defaultRecipeFolderPath != null) {
            log.info("Recipe data backup starts from the folder: {}", defaultRecipeFolderPath);
            var recipeStream = this.all().map(recipe -> new Pair<>(String.valueOf(recipe.getTitle()), recipe));
            FilesUtil.backup(defaultRecipeFolderPath, recipeStream);
            log.info("Recipe data backup completed successfully for: {}", this.all().count());
        }
    }

    @Override
    public void save(Recipe recipe) {
        if (recipe.getId() == null) {
            recipe.setId(UUID.randomUUID());
        }
        recipes.putIfAbsent(recipe.getId(), recipe);
    }

    @Override
    public Optional<Recipe> findById(UUID id) {
        return Optional.ofNullable(recipes.get(id));
    }

    @Override
    public List<Recipe> findByIds(Collection<UUID> ids) {
        return ids.stream()
                .map(recipes::get)
                .filter(Objects::nonNull)
                .toList();
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