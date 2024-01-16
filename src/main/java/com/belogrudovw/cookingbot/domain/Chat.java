package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.screen.Screen;

import java.util.Queue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.queue.CircularFifoQueue;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat {
    long id;
    // TODO: 17/01/2024 Replace RecipeObjects by RecipeId 
    Queue<Recipe> history;
    Recipe currentRecipe;
    int cookingProgress;
    RequestPreferences requestPreferences;
    Screen pivotScreen;
    boolean isAwaitCustomQuery;
    String additionalQuery;

    public Chat(long id) {
        this.id = id;
        this.history = new CircularFifoQueue<>(42);
        this.requestPreferences = RequestPreferences.builder().build();
        this.cookingProgress = 0;
    }

    public void addLastRecipeToHistory() {
        if (history.contains(currentRecipe)) {
            history.remove(currentRecipe);
            history.add(currentRecipe);
        } else {
            history.add(currentRecipe);
        }
    }
}