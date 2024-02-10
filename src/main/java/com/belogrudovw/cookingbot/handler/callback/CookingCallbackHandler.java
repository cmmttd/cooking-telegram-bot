package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.telegram.CallbackQuery;
import com.belogrudovw.cookingbot.exception.IllegalChatStateException;
import com.belogrudovw.cookingbot.exception.RecipeNotFoundException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CookingCallbackHandler extends AbstractCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.COOKING;

    Storage<Long, Chat> chatStorage;
    Storage<UUID, Recipe> recipeStorage;

    OrderService orderService;
    CookingScheduleService cookingScheduleService;
    InteractionService interactionService;

    public CookingCallbackHandler(Storage<Long, Chat> chatStorage, Storage<UUID, Recipe> recipeStorage, OrderService orderService,
                                  CookingScheduleService cookingScheduleService, InteractionService interactionService) {
        super(chatStorage);
        this.chatStorage = chatStorage;
        this.recipeStorage = recipeStorage;
        this.orderService = orderService;
        this.cookingScheduleService = cookingScheduleService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, CallbackQuery callbackQuery) {
        UUID currentRecipeId = chat.getCurrentRecipe();
        if (currentRecipeId == null) {
            throw new IllegalChatStateException(chat, "Recipe must be not null on the step: " + CURRENT_SCREEN);
        }
        chat.setAwaitCustomQuery(false);
        var button = CookingButtons.valueOf(callbackQuery.data());
        switch (button) {
            case COOKING_NEXT -> respondNextAsync(chat, currentRecipeId);
            case COOKING_PAUSE -> cookingScheduleService.cancelSchedule(chat);
            case COOKING_CANCEL -> respondCancel(chat);
        }
    }

    private void respondNextAsync(Chat chat, UUID currentRecipeId) {
        Recipe recipe = recipeStorage.findById(currentRecipeId)
                .orElseThrow(() -> new RecipeNotFoundException(chat, "Recipe not found by id: %s".formatted(currentRecipeId)));
        Mono.fromRunnable(() -> respondNextStep(chat, recipe))
                .then(Mono.fromRunnable(() -> cookingScheduleService.scheduleNexStep(chat)))
                .then(Mono.fromRunnable(() -> congratulateIfCompleted(chat, recipe))
                        .delaySubscription(Duration.ofMillis(1000)))
                .subscribe();

    }

    private void respondNextStep(Chat chat, Recipe recipe) {
        incrementProgressAndGetStep(chat, recipe)
                .map(this::buildCustomScreenForRecipeStep)
                .ifPresent(screen -> interactionService.showResponse(chat, screen));
    }

    public Optional<Recipe.Step> incrementProgressAndGetStep(Chat chat, Recipe recipe) {
        int cookingProgress = chat.getCookingProgress();
        if (recipe.getSteps().size() > cookingProgress) {
            Recipe.Step nextStep = recipe.getSteps().get(cookingProgress);
            chat.setCookingProgress(cookingProgress + 1);
            chatStorage.save(chat);
            return Optional.of(nextStep);
        }
        return Optional.empty();
    }

    private CustomScreen buildCustomScreenForRecipeStep(Recipe.Step step) {
        return CustomScreen.builder()
                .buttons(CURRENT_SCREEN.getButtons())
                .textToken(new SimpleStringToken(step.toString()))
                .build();
    }

    private void congratulateIfCompleted(Chat chat, Recipe currentRecipe) {
        if (currentRecipe.getSteps().size() == chat.getCookingProgress()) {
            log.info("User from chat {} complete the recipe {}", chat.getId(), currentRecipe.getId());
            interactionService.showResponse(chat, DefaultScreens.SUCCESS);
        }
    }

    private void respondCancel(Chat chat) {
        cookingScheduleService.cancelSchedule(chat);
        chat.setCookingProgress(0);
        interactionService.showResponse(chat, orderService.prevScreen(CURRENT_SCREEN));
    }
}