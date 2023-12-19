package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreen;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.telegram.domain.Keyboard;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.belogrudovw.cookingbot.util.KeyboardUtil.buildDefaultKeyboard;
import static com.belogrudovw.cookingbot.util.StringUtil.escapeCharacters;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicCallbackHandlerHistory implements DynamicCallbackHandler {

    private static final DefaultScreen SCREEN = DefaultScreen.HOME;
    private static final String HOME_HISTORY_CALLBACK_BASE = SCREEN.name() + "_";

    private final Storage<Long, Chat> chatStorage;
    private final RecipeService recipeService;
    private final OrderService orderService;
    private final ResponseService responseService;

    @Override
    public String getPattern() {
        return HOME_HISTORY_CALLBACK_BASE + "\\d+";
    }

    @Override
    public void handle(UserAction action) {
        log.debug("History dynamic callback");
        long chatId = action.getChatId();
        UserAction.CallbackQuery callbackQuery = action.callbackQuery().orElseThrow();
        chatStorage.get(chatId)
                .map(chat -> mapToScreen(chat, callbackQuery))
                .ifPresent(nextScreen -> respond(nextScreen, chatId, callbackQuery));
    }

    private Screen mapToScreen(Chat chat, UserAction.CallbackQuery callbackQuery) {
        String[] recipeIdString = callbackQuery.data().split(HOME_HISTORY_CALLBACK_BASE);
        long recipeId = Long.parseLong(recipeIdString[1]);
        Recipe recipe = recipeService.getById(recipeId);
        chat.setCurrentRecipe(recipe);
        chat.setCookingProgress(0);
        chatStorage.save(chat);
        Screen nextScreen = orderService.nextScreen(SCREEN);
        return CustomScreen.builder()
                .buttons(nextScreen.getButtons())
                .text(escapeCharacters("*" + recipe.getTitle() + "*" + "\n" + recipe.getShortDescription()))
                .build();
    }

    private void respond(Screen nextScreen, long chatId, UserAction.CallbackQuery callbackQuery) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.editMessage(chatId, callbackQuery.message().messageId(), nextScreen.getText(), keyboard);
    }
}
