package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat {
    @Id
    long id;
    String username;
    Queue<UUID> history = new CircularFifoQueue<>(20);
    UUID currentRecipe;
    AtomicInteger imageProgress;
    int cookingProgress;
    RequestPreferences requestPreferences;
    DefaultScreens pivotScreen;
    boolean isAwaitCustomQuery;
    String additionalQuery;
    AtomicInteger lastUsedMessageId;
    AtomicInteger lastUsedImageMessageId;

    public Chat(long id, String username) {
        this.id = id;
        this.username = username;
        this.requestPreferences = RequestPreferences.builder().build();
        this.cookingProgress = 0;
        this.imageProgress = new AtomicInteger();
        this.lastUsedMessageId = new AtomicInteger();
        this.lastUsedImageMessageId = new AtomicInteger();
    }

    public void addLastRecipeToHistory() {
        if (history.contains(currentRecipe)) {
            history.remove(currentRecipe);
            history.add(currentRecipe);
        } else {
            history.add(currentRecipe);
        }
    }

    @JsonSetter("history")
    public void setHistory(List<UUID> stringList) {
        history.clear();
        history.addAll(stringList);
    }
}