package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.config.RecoveryProperties;
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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
@ConditionalOnProperty(prefix = "storage", name = "source", havingValue = "in-memory")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecipeStorageInMemory implements Storage<UUID, Recipe> {

    Map<UUID, Recipe> recipes = new ConcurrentHashMap<>();
    RecoveryProperties recoveryProperties;

    @PostConstruct
    void recover() {
        if (recoveryProperties.isNeeded() && recoveryProperties.defaultRecipesPath() != null) {
            log.info("Recipe recovery starts from the folder: {}", recoveryProperties.defaultRecipesPath());
            int recovered = FilesUtil.recover(recoveryProperties.defaultRecipesPath(), Recipe.class, this::save);
            log.info("Recipe recovery completed successfully for: {}", recovered);
        }
    }

    @PreDestroy
    void backup() {
        if (recoveryProperties.isNeeded() && recoveryProperties.defaultRecipesPath() != null) {
            log.info("Recipe data backup starts from the folder: {}", recoveryProperties.defaultRecipesPath());
            var recipeStream = recipes.entrySet().stream()
                    .map(entry -> new Pair<>(String.valueOf(entry.getKey()), entry.getValue()));
            FilesUtil.backup(recoveryProperties.defaultRecipesPath(), recipeStream);
            log.info("Recipe data backup completed successfully for: {}", size());
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

    @Override
    public long size() {
        return recipes.size();
    }
}