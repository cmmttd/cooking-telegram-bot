package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.telegram.domain.Keyboard;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildDefaultKeyboard;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CookingScheduleServiceInMemory implements CookingScheduleService {

    ConcurrentNavigableMap<Long, Map<Long, RecipeStepTask>> tasksTimeline = new ConcurrentSkipListMap<>();
    Map<Long, Set<Long>> timestampsByChatId = new ConcurrentHashMap<>();

    ChatService chatService;
    ResponseService responseService;
    RecipeService recipeService;

    // TODO: 29/12/2023 Rethink all approach for delayed tasks execution
    //  1. perhaps, do not duplicate logic and use callback handler as reference
    @Override
    public void scheduleNexStep(Chat chat) {
        cancelSchedule(chat);
        UUID recipeId = chat.getCurrentRecipe().getId();
        long chatId = chat.getId();
        timestampsByChatId.put(chatId, new HashSet<>());
        List<Recipe.CookingStep> steps = chat.getCurrentRecipe().getSteps();
        int cookingProgress = chat.getCookingProgress();
        if (cookingProgress < steps.size()) {
            long prevStepOffset = cookingProgress > 0 ? steps.get(cookingProgress - 1).offset() : 0;
            long baseOffset = TimeUnit.MINUTES.toMillis(prevStepOffset);
            long nowMillis = System.currentTimeMillis() - baseOffset;
            for (int i = cookingProgress; i < steps.size(); i++) {
                Recipe.CookingStep step = steps.get(i);
                long nextStepOffsetMillis = TimeUnit.MINUTES.toMillis(step.offset());
                long taskStartTime = nowMillis + nextStepOffsetMillis;
                tasksTimeline.putIfAbsent(taskStartTime, new HashMap<>());
                tasksTimeline.get(taskStartTime).put(chatId, new RecipeStepTask(recipeId, i));
                timestampsByChatId.get(chatId).add(taskStartTime);
            }
            log.info(">>> tasks timeline: {}", tasksTimeline.entrySet().stream()
                    .map(x -> Instant.ofEpochMilli(x.getKey()).atZone(ZoneId.systemDefault()) + " - " + x.getValue())
                    .collect(Collectors.joining(", \n", "\n", "\n")));
            log.info(">>> timestamps by chat: {}", timestampsByChatId);
        }
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
        if (!expiredTasks.isEmpty())
            log.info("Start scheduler");
        Map<Long, UUID> completedTaskMap = new HashMap<>();
        expiredTasks.forEach((timestamp, tasks) -> {
            tasks.forEach((chatId, task) -> {
                sendNextStepMessage(chatId, task);
                updateCookingProgress(chatId, task);
                removeTimestamp(chatId, timestamp);
                completedTaskMap.put(chatId, task.recipeId());
            });
            tasksTimeline.remove(timestamp);
        });
        completedTaskMap.forEach(this::sendCompleteMessage);
    }

    private void sendNextStepMessage(long chatId, RecipeStepTask task) {
        Recipe recipe = recipeService.findById(task.recipeId());
        Recipe.CookingStep step = recipe.getSteps().get(task.stepCount());
        Keyboard keyboard = buildDefaultKeyboard(List.of(CookingButtons.COOKING_NEXT, CookingButtons.COOKING_CANCEL));
        responseService.sendMessage(chatId, step.toString(), keyboard);
    }

    private void updateCookingProgress(long chatId, RecipeStepTask task) {
        Chat chat = chatService.findById(chatId);
        chat.setCookingProgress(task.stepCount() + 1);
        chatService.save(chat);
    }

    private void removeTimestamp(long chatId, long timestamp) {
        Set<Long> timestamps = timestampsByChatId.get(chatId);
        timestamps.remove(timestamp);
    }

    private void sendCompleteMessage(long chatId, UUID recipeId) {
        if (timestampsByChatId.get(chatId).isEmpty()) {
            log.info("User from chat {} has been finished the recipe: {}", chatId, recipeId);
            timestampsByChatId.remove(chatId);
            DefaultScreens nextScreen = DefaultScreens.SUCCESS;
            Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
            responseService.sendMessage(chatId, nextScreen.getText(), keyboard);
        }
    }

    private record RecipeStepTask(UUID recipeId, int stepCount) {
    }
}


















