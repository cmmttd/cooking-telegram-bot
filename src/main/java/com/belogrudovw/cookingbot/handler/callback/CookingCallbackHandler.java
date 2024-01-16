package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;

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
    InteractionService interactionService;

    public CookingCallbackHandler(ChatService chatService, OrderService orderService, CookingScheduleService cookingScheduleService,
                                  InteractionService interactionService) {
        super(chatService);
        this.orderService = orderService;
        this.cookingScheduleService = cookingScheduleService;
        this.chatService = chatService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        Recipe currentRecipe = chat.getCurrentRecipe();
        if (currentRecipe == null) {
            throw new IllegalChatStateException(chat, "Recipe must be not null on the step: " + CURRENT_SCREEN);
        }
        chat.setAwaitCustomQuery(false);
        var button = CookingButtons.valueOf(callbackQuery.data());
        switch (button) {
            case COOKING_NEXT -> respondNextAsync(chat, currentRecipe);
            case COOKING_PAUSE -> cookingScheduleService.cancelSchedule(chat);
            case COOKING_CANCEL -> respondCancel(chat);
        }
        chatService.save(chat);
    }

    private void respondNextAsync(Chat chat, Recipe currentRecipe) {
        Mono.fromRunnable(() -> respondNextStep(chat))
                .then(Mono.fromRunnable(() -> cookingScheduleService.scheduleNexStep(chat)))
                .then(Mono.fromRunnable(() -> congratulateIfCompleted(chat, currentRecipe))
                        .delaySubscription(Duration.ofMillis(1000)))
                .subscribe();
    }

    private void respondNextStep(Chat chat) {
        chatService.incrementProgressAndGetStep(chat)
                .map(this::buildCustomScreenForRecipeStep)
                .ifPresent(screen -> interactionService.showResponse(chat, screen));
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