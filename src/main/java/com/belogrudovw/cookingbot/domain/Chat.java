package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.screen.Screen;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat {
    long id;
    GenerationMode mode;
    Recipe currentRecipe;
    int cookingProgress;
    List<Recipe> history;
    RequestProperties requestProperties;
    Screen pivotScreen;

    public Chat(long id) {
        this.id = id;
        this.history = new ArrayList<>();
        this.requestProperties = RequestProperties.builder().build();
        this.cookingProgress = 0;
    }

    // TODO: 16/12/2023 Replace by queue
    public void addLastRecipeToHistory() {
        if (!history.contains(currentRecipe)) {
            history.add(currentRecipe);
        }
    }
}