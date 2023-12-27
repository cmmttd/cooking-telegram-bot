package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CookingButtons;
import com.belogrudovw.cookingbot.domain.buttons.CustomCallbackButton;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreen;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.telegram.domain.Keyboard;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Arrays;
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
public class CookingCallbackHandler implements CallbackHandler {

    private static final Screen SCREEN = DefaultScreen.COOKING;

    private final Storage<Long, Chat> chatStorage;
    private final OrderService orderService;
    private final ResponseService responseService;

    @Override
    public Set<String> getSupported() {
        return Arrays.stream(SCREEN.getButtons())
                .map(CallbackButton::getCallbackData)
                .collect(Collectors.toSet());
    }

    @Override
    public void handle(UserAction action) {
        log.debug("Cooking handler called for: {}", action);
        long chatId = action.getChatId();
        UserAction.CallbackQuery callbackQuery = action.callbackQuery().orElseThrow();
        chatStorage.get(chatId)
                .filter(chat -> chat.getCurrentRecipe() != null)
                .map(chat -> mapToScreen(chat, callbackQuery))
                .ifPresentOrElse(nextScreen -> respond(nextScreen, chatId),
                        () -> respond(orderService.getFirst(), chatId));
    }

    private Screen mapToScreen(Chat chat, UserAction.CallbackQuery callbackQuery) {
        var button = CookingButtons.valueOf(callbackQuery.data());
        return switch (button) {
            case COOKING_NEXT -> {
                Recipe recipe = chat.getCurrentRecipe();
                int cookingProgress = chat.getCookingProgress();
                if (cookingProgress < recipe.getSteps().size()) {
                    Recipe.CookingStep step = recipe.getSteps().get(cookingProgress);
                    chat.setCurrentRecipe(recipe);
                    chat.setCookingProgress(cookingProgress + 1);
                    chatStorage.save(chat);
                    yield CustomScreen.builder()
                            .buttons(SCREEN.getButtons())
                            .text(escapeCharacters("*" + step.title() + "*" + "\n" + step.description()))
                            .build();
                } else {
                    log.info("User from chat {} complete the recipe {}", chat.getId(), recipe.getId());
                    CallbackButton[] buttons = {CustomCallbackButton.builder()
                            .text(escapeCharacters("💕"))
                            .callbackData("SUCCESS")
                            .build()};
                    yield CustomScreen.builder()
                            .buttons(buttons)
                            .text(escapeCharacters("Congratulations! You're cooking master!\nOne more time?"))
                            .build();
                }
            }
            case COOKING_CANCEL -> {
                chat.setCurrentRecipe(null);
                chat.setCookingProgress(0);
                chatStorage.save(chat);
                yield chat.getPivotScreen();
            }
        };
    }

    private void respond(Screen nextScreen, long chatId) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.sendMessage(chatId, nextScreen.getText(), keyboard);
    }
}