package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CustomCallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.HomeButtons;
import com.belogrudovw.cookingbot.domain.enums.GenerationMode;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreen;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.telegram.domain.Keyboard;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.belogrudovw.cookingbot.util.KeyboardUtil.buildDefaultKeyboard;
import static com.belogrudovw.cookingbot.util.StringUtil.escapeCharacters;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeCallbackHandler implements CallbackHandler {

    private static final Screen SCREEN = DefaultScreen.HOME;
    public static final String HOME_HISTORY_CALLBACK_BASE = DefaultScreen.HOME.name() + "_";

    private final Storage<Long, Chat> chatStorage;
    private final OrderService orderService;
    private final ResponseService responseService;
    private final RecipeService recipeService;

    @Override
    public Set<String> getSupported() {
        return Arrays.stream(SCREEN.getButtons())
                .map(CallbackButton::getCallbackData)
                .collect(Collectors.toSet());
    }

    @Override
    public void handle(UserAction action) {
        log.debug("Home handler called for: {}", action);
        long chatId = action.getChatId();
        UserAction.CallbackQuery callbackQuery = action.callbackQuery().orElseThrow();
        chatStorage.get(chatId)
                .map(chat -> mapToScreen(chat, callbackQuery))
                .ifPresentOrElse(nextScreen -> respond(nextScreen, chatId, callbackQuery),
                        () -> respond(orderService.getFirst(), chatId, callbackQuery));
    }

    private Screen mapToScreen(Chat chat, UserAction.CallbackQuery callbackQuery) {
        var button = HomeButtons.valueOf(callbackQuery.data());
        chat.setPivotScreen(SCREEN);
        chatStorage.save(chat);
        return switch (button) {
            case HOME_REQUEST_NEW -> {
                chat.setMode(GenerationMode.NEW);
                Recipe newRecipe = recipeService.requestNew(chat.getProperty());
                chat.setCurrentRecipe(newRecipe);
                chat.setCookingProgress(0);
                chatStorage.save(chat);
                Screen nextScreenTemplate = orderService.nextScreen(SCREEN);
                yield CustomScreen.builder()
                        .buttons(nextScreenTemplate.getButtons())
                        .text(escapeCharacters("*" + newRecipe.getTitle() + "*" + "\n" + newRecipe.getShortDescription()))
                        .build();
            }
            case HOME_EXISTS -> {
                chat.setMode(GenerationMode.EXISTING);
                Recipe newRecipe = recipeService.getRandom(chat);
                chat.setCurrentRecipe(newRecipe);
                chat.setCookingProgress(0);
                chatStorage.save(chat);
                Screen nextScreenTemplate = orderService.nextScreen(SCREEN);
                yield CustomScreen.builder()
                        .buttons(nextScreenTemplate.getButtons())
                        .text(escapeCharacters("*" + newRecipe.getTitle() + "*" + "\n" + newRecipe.getShortDescription()))
                        .build();
            }
            case HOME_HISTORY -> {
                List<Recipe> history = chat.getHistory();
                List<CallbackButton> buttons = new ArrayList<>();
                for (Recipe recipe : history) {
                    CustomCallbackButton newButton = CustomCallbackButton.builder()
                            .text(recipe.getTitle())
                            .callbackData(HOME_HISTORY_CALLBACK_BASE + recipe.getId())
                            .build();
                    buttons.add(newButton);
                }
                CallbackButton backButton = Arrays.stream(orderService.nextScreen(SCREEN).getButtons())
                        .filter(x -> x.getText().equals("Back"))
                        .findFirst()
                        .orElseThrow();
                buttons.add(backButton);
                yield CustomScreen.builder()
                        .text("Pick from the history:")
                        .buttons(buttons.toArray(CallbackButton[]::new))
                        .build();
            }
            case HOME_BACK -> orderService.prevScreen(SCREEN);
        };
    }

    private void respond(Screen nextScreen, long chatId, UserAction.CallbackQuery callbackQuery) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.editMessage(chatId, callbackQuery.message().messageId(), nextScreen.getText(), keyboard);
    }
}