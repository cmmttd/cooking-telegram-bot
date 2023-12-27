package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        this.chatService = chatService;
        this.orderService = orderService;
        this.cookingScheduleService = cookingScheduleService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        long chatId = chat.getId();
        if (chat.getCurrentRecipe() == null) {
            throw new IllegalChatStateException(chatId, "Recipe must be not null on the step: " + CURRENT_SCREEN);
        }
        var button = CookingButtons.valueOf(callbackQuery.data());
        switch (button) {
            case COOKING_NEXT -> {
                chatService.nextRecipeStep(chat)
                        .map(this::buildCustomScreenForRecipeStep)
                        .ifPresent(screen -> respond(chatId, screen));
                if (chat.getCurrentRecipe().getSteps().size() == chat.getCookingProgress()) {
                    respondWithSuccessMessage(chatId, chat.getCurrentRecipe().getId());
                }
                cookingScheduleService.scheduleNexStep(chat);
            }
            case COOKING_CANCEL -> {
                cookingScheduleService.cancelSchedule(chat);
                respond(chatId, orderService.prevScreen(CURRENT_SCREEN));
            }
        }
    }

    private CustomScreen buildCustomScreenForRecipeStep(Recipe.CookingStep step) {
        return CustomScreen.builder()
                .buttons(CURRENT_SCREEN.getButtons())
                .text(step.toString())
                .build();
    }

    private void respondWithSuccessMessage(long chatId, UUID recipeId) {
        log.info("User from chat {} complete the recipe {}", chatId, recipeId);
        respond(chatId, DefaultScreens.SUCCESS);
    }
}