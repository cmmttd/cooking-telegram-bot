package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.SpinPickRecipeButtons;
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
import com.belogrudovw.cookingbot.service.RecipeService;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpinPickRecipeCallbackHandler extends AbstractCallbackHandler {

    static final Screen CURRENT_SCREEN = DefaultScreens.SPIN_PICK_RECIPE;

    ChatService chatService;
    OrderService orderService;
    RecipeService recipeService;
    CookingScheduleService cookingScheduleService;
    InteractionService interactionService;


    public SpinPickRecipeCallbackHandler(ChatService chatService, OrderService orderService, RecipeService recipeService,
                                         CookingScheduleService cookingScheduleService, InteractionService interactionService) {
        super(chatService);
        this.chatService = chatService;
        this.orderService = orderService;
        this.recipeService = recipeService;
        this.cookingScheduleService = cookingScheduleService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        if (chat.getCurrentRecipe() == null) {
            throw new IllegalChatStateException(chat, "Recipe must be not null on the step: " + CURRENT_SCREEN);
        }
        var button = SpinPickRecipeButtons.valueOf(callbackQuery.data());
        final int messageId = callbackQuery.message().messageId();
        switch (button) {
            case SPIN_PICK_RECIPE_START -> {
                hideButtons(chat, messageId);
                cookingScheduleService.scheduleNexStep(chat);
            }
            case SPIN_PICK_RECIPE_SPIN -> respondNewRecipe(chat, messageId);
            case SPIN_PICK_RECIPE_BACK -> interactionService.showResponse(chat, messageId, orderService.prevScreen(CURRENT_SCREEN));
        }
    }

    private void hideButtons(Chat chat, int messageId) {
        String recipeString = chat.getCurrentRecipe().toFormattedString(chat.getRequestPreferences().getLanguage());
        CustomScreen hidedButtonsScreen = CustomScreen.builder()
                .buttons(Collections.emptyList())
                .textToken(new SimpleStringToken(recipeString))
                .build();
        interactionService.showResponse(chat, messageId, hidedButtonsScreen);
    }

    private void respondNewRecipe(Chat chat, int messageId) {
        Mono.fromRunnable(() -> interactionService.showSpinner(chat, messageId))
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(300)))
                .map(recipe -> {
                    chatService.setNewRecipe(chat, recipe);
                    return recipe;
                })
                .map(recipe -> CustomScreen.builder()
                        .buttons(CURRENT_SCREEN.getButtons())
                        .textToken(new SimpleStringToken(recipe.toFormattedString(chat.getRequestPreferences().getLanguage())))
                        .build())
                .subscribe(screen -> interactionService.showResponse(chat, messageId, screen));
    }
}