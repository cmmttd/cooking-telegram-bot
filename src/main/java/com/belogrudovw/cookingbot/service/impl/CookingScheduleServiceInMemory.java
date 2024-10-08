package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.exception.RecipeNotFoundException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.COOKING;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CookingScheduleServiceInMemory implements CookingScheduleService {

    ConcurrentNavigableMap<Long, Map<Long, RecipeStepTask>> tasksTimeline = new ConcurrentSkipListMap<>();
    Map<Long, Set<Long>> timestampsByChatId = new ConcurrentHashMap<>();

    Storage<Long, Chat> chatStorage;
    Storage<UUID, Recipe> recipeStorage;
    InteractionService interactionService;

    // TODO: 29/12/2023 Issue#18 Rethink all approach for delayed tasks execution
    //  1. perhaps, do not duplicate logic and use callback handler as reference
    @Override
    public void scheduleNexStep(Chat chat) {
        cancelSchedule(chat);
        UUID recipeId = chat.getCurrentRecipe();
        long chatId = chat.getId();
        int cookingProgress = chat.getCookingProgress();
        recipeStorage.findById(recipeId)
                .map(Recipe::getSteps)
                .filter(steps -> cookingProgress < steps.size())
                .ifPresent(steps -> {
                    long prevStepOffset = cookingProgress > 0 ? steps.get(cookingProgress - 1).offset() : 0;
                    long baseOffset = TimeUnit.MINUTES.toMillis(prevStepOffset);
//                    long baseOffset = TimeUnit.MINUTES.toMillis(prevStepOffset) / 100;
                    long nowMillis = System.currentTimeMillis() - baseOffset;
                    timestampsByChatId.putIfAbsent(chatId, new HashSet<>());
                    for (int i = cookingProgress; i < steps.size(); i++) {
                        Recipe.Step step = steps.get(i);
                        long nextStepOffsetMillis = TimeUnit.MINUTES.toMillis(step.offset());
//                        long nextStepOffsetMillis = TimeUnit.MINUTES.toMillis(step.offset()) / 100;
                        long taskStartTime = nowMillis + nextStepOffsetMillis;
                        tasksTimeline.putIfAbsent(taskStartTime, new HashMap<>());
                        tasksTimeline.get(taskStartTime).put(chatId, new RecipeStepTask(recipeId, i));
                        timestampsByChatId.get(chatId).add(taskStartTime);
                    }
                });
    }

    @Override
    public void cancelSchedule(Chat chat) {
        long chatId = chat.getId();
        if (timestampsByChatId.containsKey(chatId)) {
            Set<Long> timestamps = timestampsByChatId.get(chatId);
            timestamps.forEach(timestamp -> {
                var tasksForTimestamp = tasksTimeline.get(timestamp);
                tasksForTimestamp.remove(chatId);
                if (tasksForTimestamp.isEmpty()) {
                    tasksTimeline.remove(timestamp);
                }
            });
            timestampsByChatId.remove(chatId);
        }
    }

    @Scheduled(fixedRate = 1000)
    private void scheduledTaskExecution() {
        var expiredTasks = tasksTimeline.headMap(System.currentTimeMillis());
        if (!expiredTasks.isEmpty()) {
            log.info("Start scheduler");
            log.info(">>> timestamps by chat: {}", timestampsByChatId);
            log.debug(">>> tasks timeline: {}", tasksTimeline.entrySet().stream()
                    .map(x -> Instant.ofEpochMilli(x.getKey()).atZone(ZoneId.systemDefault()) + " - " + x.getValue())
                    .collect(Collectors.joining(", \n", "\n", "\n")));
        }
        Map<Long, UUID> completedTaskMap = new HashMap<>();
        expiredTasks.forEach((timestamp, tasks) -> {
            tasks.forEach((chatId, task) -> chatStorage.findById(chatId)
                    .ifPresent(chat -> {
                        sendNextRecipeStep(chat, task);
                        updateCookingProgress(chatId, task);
                        removeTimestamp(chatId, timestamp);
                        completedTaskMap.put(chatId, task.recipeId());
                    }));
            tasksTimeline.remove(timestamp);
        });
        completedTaskMap.forEach(this::sendCompleteMessage);
    }

    private void sendNextRecipeStep(Chat chat, RecipeStepTask task) {
        recipeStorage.findById(task.recipeId())
                .map(recipe -> recipe.getSteps().get(task.stepCount()))
                .map(step -> CustomScreen.builder()
                        .buttons(COOKING.getButtons())
                        .textToken(new SimpleStringToken(step.toString()))
                        .build())
                .ifPresentOrElse(screen -> interactionService.showResponse(chat, screen),
                        () -> {
                            throw new RecipeNotFoundException(chat, "Recipe not found for: %s".formatted(task.recipeId()));
                        });
    }

    private void updateCookingProgress(long chatId, RecipeStepTask task) {
        chatStorage.findById(chatId)
                .map(chat -> {
                    chat.setCookingProgress(task.stepCount() + 1);
                    return chat;
                })
                .ifPresent(chatStorage::save);
    }

    private void removeTimestamp(long chatId, long timestamp) {
        Set<Long> timestamps = timestampsByChatId.get(chatId);
        timestamps.remove(timestamp);
    }

    private void sendCompleteMessage(long chatId, UUID recipeId) {
        if (timestampsByChatId.get(chatId).isEmpty()) {
            log.info("User from chat {} has been finished the recipe: {}", chatId, recipeId);
            timestampsByChatId.remove(chatId);
            chatStorage.findById(chatId)
                    .ifPresent(chat -> interactionService.showResponse(chat, DefaultScreens.SUCCESS));
        }
    }

    private record RecipeStepTask(UUID recipeId, int stepCount) {
    }
}