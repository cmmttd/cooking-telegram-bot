package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.GenerationMode;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.SpinPickRecipeButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpinPickRecipeCallbackHandler extends AbstractCallbackHandler {

    static final Screen CURRENT_SCREEN = DefaultScreens.SPIN_PICK_RECIPE;

    ChatService chatService;
    OrderService orderService;
    RecipeService recipeService;
    CookingScheduleService cookingScheduleService;

    public SpinPickRecipeCallbackHandler(ResponseService responseService, ChatService chatService, OrderService orderService,
                                         RecipeService recipeService, CookingScheduleService cookingScheduleService) {
        super(responseService, chatService);
        this.chatService = chatService;
        this.orderService = orderService;
        this.recipeService = recipeService;
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
        var button = SpinPickRecipeButtons.valueOf(callbackQuery.data());
        switch (button) {
            case SPIN_PICK_RECIPE_START -> cookingScheduleService.scheduleNexStep(chat);
            case SPIN_PICK_RECIPE_SPIN -> respondNewRecipe(chat, callbackQuery);
            case SPIN_PICK_RECIPE_BACK -> {
                Screen nextScreen = orderService.prevScreen(CURRENT_SCREEN);
                respond(chatId, callbackQuery.message().messageId(), nextScreen);
            }
        }
    }

    private void respondNewRecipe(Chat chat, UserAction.CallbackQuery callbackQuery) {
        Recipe recipe = chat.getMode() == GenerationMode.NEW
                ? recipeService.requestNew(chat)
                : recipeService.getRandom(chat);
        chatService.setNewRecipe(chat, recipe);
        CustomScreen screen = CustomScreen.builder()
                .buttons(CURRENT_SCREEN.getButtons())
                .text(recipe.toString())
                .build();
        respond(chat.getId(), callbackQuery.message().messageId(), screen);
    }
}