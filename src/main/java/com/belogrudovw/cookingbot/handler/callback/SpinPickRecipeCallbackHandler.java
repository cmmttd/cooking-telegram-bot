package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.SpinPickRecipeButtons;
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
public class SpinPickRecipeCallbackHandler implements CallbackHandler {

    private static final Screen SCREEN = DefaultScreen.SPIN_PICK_RECIPE;

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
        log.debug("Pick recipe handler called for: {}", action);
        long chatId = action.getChatId();
        UserAction.CallbackQuery callbackQuery = action.callbackQuery().orElseThrow();
        chatStorage.get(chatId)
                .filter(chat -> chat.getCurrentRecipe() != null)
                .map(chat -> mapToScreen(chat, callbackQuery))
                .ifPresentOrElse(nextScreen -> respond(nextScreen, chatId, callbackQuery),
                        () -> respond(orderService.getFirst(), chatId, callbackQuery));
    }

    private Screen mapToScreen(Chat chat, UserAction.CallbackQuery callbackQuery) {
        var button = SpinPickRecipeButtons.valueOf(callbackQuery.data());
        return switch (button) {
            case SPIN_PICK_RECIPE_SPIN -> {
                chat.addLastRecipeToHistory();
                Recipe recipe = chat.getMode() == GenerationMode.NEW
                        ? recipeService.requestNew(chat.getProperty())
                        : recipeService.getRandom(chat);
                chat.setCurrentRecipe(recipe);
                chat.setCookingProgress(0);
                chat.addLastRecipeToHistory();
                chatStorage.save(chat);
                yield CustomScreen.builder()
                        .buttons(SCREEN.getButtons())
                        .text(escapeCharacters("*" + recipe.getTitle() + "*" + "\n" + recipe.getShortDescription()))
                        .build();
            }
            case SPIN_PICK_RECIPE_START -> {
                chat.addLastRecipeToHistory();
                Recipe recipe = chat.getCurrentRecipe();
                Screen nextScreenTemplate = orderService.nextScreen(SCREEN);
                int cookingProgress = chat.getCookingProgress();
                Recipe.CookingStep step = recipe.getSteps().get(cookingProgress);
                chat.setCurrentRecipe(recipe);
                chat.setCookingProgress(cookingProgress + 1);
                chatStorage.save(chat);
//                delayedTaskService.createPostponedTask(chat.getId())
                yield CustomScreen.builder()
                        .buttons(nextScreenTemplate.getButtons())
                        .text(escapeCharacters("*" + step.title() + "*" + "\n" + step.description()))
                        .build();
            }
            case SPIN_PICK_RECIPE_BACK -> orderService.prevScreen(SCREEN);
        };
    }

    private void respond(Screen nextScreen, long chatId, UserAction.CallbackQuery callbackQuery) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.editMessage(chatId, callbackQuery.message().messageId(), nextScreen.getText(), keyboard);
    }
}