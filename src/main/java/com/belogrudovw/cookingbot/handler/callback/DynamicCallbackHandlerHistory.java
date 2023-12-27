package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DynamicCallbackHandlerHistory extends AbstractDynamicCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.HOME;
    static final String HOME_HISTORY_CALLBACK_BASE = CURRENT_SCREEN.name() + "_";
    static final String CALLBACK_PATTERN = HOME_HISTORY_CALLBACK_BASE + "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";

    ChatService chatService;
    RecipeService recipeService;
    OrderService orderService;

    public DynamicCallbackHandlerHistory(ResponseService responseService, ChatService chatService, RecipeService recipeService,
                                         OrderService orderService) {
        super(responseService, chatService);
        this.chatService = chatService;
        this.recipeService = recipeService;
        this.orderService = orderService;
    }

    @Override
    public String getPattern() {
        return CALLBACK_PATTERN;
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        // TODO: 20/12/2023 Wrap with try-catch
        String[] recipeIdString = callbackQuery.data().split(HOME_HISTORY_CALLBACK_BASE);
        UUID recipeId = UUID.fromString(recipeIdString[1]);
        Recipe recipe = recipeService.findById(recipeId);
        chatService.setNewRecipe(chat, recipe);
        Screen screen = CustomScreen.builder()
                .buttons(orderService.nextScreen(CURRENT_SCREEN).getButtons())
                .text(recipe.toString())
                .build();
        respond(chat.getId(), callbackQuery.message().messageId(), screen);
    }
}