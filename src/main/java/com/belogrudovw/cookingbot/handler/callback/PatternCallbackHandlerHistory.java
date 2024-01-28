package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.telegram.CallbackQuery;
import com.belogrudovw.cookingbot.exception.RecipeNotFoundException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatternCallbackHandlerHistory extends AbstractPatternCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.HOME;
    static final String HOME_HISTORY_CALLBACK_BASE = CURRENT_SCREEN.name() + "_";
    static final String CALLBACK_PATTERN = HOME_HISTORY_CALLBACK_BASE + "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";

    ChatService chatService;
    Storage<UUID, Recipe> recipeStorage;
    OrderService orderService;
    InteractionService interactionService;

    public PatternCallbackHandlerHistory(ChatService chatService, Storage<Long, Chat> chatStorage, Storage<UUID, Recipe> recipeStorage,
                                         OrderService orderService, InteractionService interactionService) {
        super(chatStorage);
        this.chatService = chatService;
        this.recipeStorage = recipeStorage;
        this.orderService = orderService;
        this.interactionService = interactionService;
    }

    @Override
    public String getPattern() {
        return CALLBACK_PATTERN;
    }

    @Override
    public void handleCallback(Chat chat, CallbackQuery callbackQuery) {
        // TODO: 20/12/2023 Wrap with try-catch
        String[] recipeIdString = callbackQuery.data().split(HOME_HISTORY_CALLBACK_BASE);
        UUID recipeId = UUID.fromString(recipeIdString[1]);
        Recipe recipe = recipeStorage.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(chat, "Recipe not found by id: %s".formatted(recipeId)));
        chatService.setNewRecipe(chat, recipe);
        CustomScreen screen = CustomScreen.builder()
                .buttons(orderService.nextScreen(CURRENT_SCREEN).getButtons())
                .textToken(new SimpleStringToken(recipe.toFormattedString(chat.getRequestPreferences().getLanguage())))
                .build();
        interactionService.showResponse(chat, callbackQuery.message().messageId(), screen);
    }
}