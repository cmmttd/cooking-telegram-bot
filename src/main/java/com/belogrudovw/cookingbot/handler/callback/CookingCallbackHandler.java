package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;

import java.time.Duration;
import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CookingCallbackHandler extends AbstractCallbackHandler {

    static final Screen CURRENT_SCREEN = DefaultScreens.COOKING;

    ChatService chatService;
    OrderService orderService;
    CookingScheduleService cookingScheduleService;

    public CookingCallbackHandler(ResponseService responseService, ChatService chatService, OrderService orderService,
                                  CookingScheduleService cookingScheduleService) {
        super(responseService, chatService);
        this.orderService = orderService;
        this.cookingScheduleService = cookingScheduleService;
        this.chatService = chatService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        long chatId = chat.getId();
        Recipe currentRecipe = chat.getCurrentRecipe();
        if (currentRecipe == null) {
            throw new IllegalChatStateException(chatId, "Recipe must be not null on the step: " + CURRENT_SCREEN);
        }
        var button = CookingButtons.valueOf(callbackQuery.data());
        switch (button) {
            case COOKING_NEXT -> {
                Mono.fromRunnable(() -> respondNextStep(chat, chatId))
                        .then(Mono.fromRunnable(() -> cookingScheduleService.scheduleNexStep(chat)))
                        .then(Mono.fromRunnable(() -> congratulateIfCompleted(chat, currentRecipe, chatId))
                                .delaySubscription(Duration.ofMillis(1000)))
                        .subscribe();
            }
            case COOKING_PAUSE -> cookingScheduleService.cancelSchedule(chat);
            case COOKING_CANCEL -> {
                cookingScheduleService.cancelSchedule(chat);
                chat.setCurrentRecipe(null);
                chat.setCookingProgress(0);
                respond(chatId, orderService.prevScreen(CURRENT_SCREEN));
            }
        }
    }

    private void respondNextStep(Chat chat, long chatId) {
        chatService.incrementProgressAndGetStep(chat)
                .map(this::buildCustomScreenForRecipeStep)
                .ifPresent(screen -> respond(chatId, screen));
    }

    private void congratulateIfCompleted(Chat chat, Recipe currentRecipe, long chatId) {
        if (currentRecipe.getSteps().size() == chat.getCookingProgress()) {
            log.info("User from chat {} complete the recipe {}", chatId, currentRecipe.getId());
            respond(chat.getId(), DefaultScreens.SUCCESS);
        }
    }

    private CustomScreen buildCustomScreenForRecipeStep(Recipe.Step step) {
        return CustomScreen.builder()
                .buttons(CURRENT_SCREEN.getButtons())
                .text(step.toString())
                .build();
    }
}