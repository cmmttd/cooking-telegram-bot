package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestProperties;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CustomCallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.HomeButtons;
import com.belogrudovw.cookingbot.domain.displayable.Navigational;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeCallbackHandler extends AbstractCallbackHandler {

    public static final String HOME_HISTORY_CALLBACK_BASE = DefaultScreens.HOME.name() + "_";
    static final Screen CURRENT_SCREEN = DefaultScreens.HOME;

    ChatService chatService;
    OrderService orderService;
    RecipeService recipeService;

    public HomeCallbackHandler(ResponseService responseService, ChatService chatService, OrderService orderService,
                               RecipeService recipeService) {
        super(responseService, chatService);
        this.chatService = chatService;
        this.orderService = orderService;
        this.recipeService = recipeService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        long chatId = chat.getId();
        RequestProperties requestProperties = chat.getRequestProperties();
        if (requestProperties.isEmpty()) {
            String errorMessage = "Must have a non empty properties for step: %s. But properties there are: %s"
                    .formatted(CURRENT_SCREEN, requestProperties);
            throw new IllegalChatStateException(chatId, errorMessage);
        }
        var button = HomeButtons.valueOf(callbackQuery.data());
        chat.setPivotScreen(CURRENT_SCREEN);
        chat.setAwaitCustomQuery(false);
        final int messageId = callbackQuery.message().messageId();
        switch (button) {
            case HOME_CUSTOM -> {
                chat.setAwaitCustomQuery(true);
                respond(chat.getId(), messageId, buildNextScreenForCustomQuery());
            }
            case HOME_RANDOM -> respondAsync(chat, messageId);
            case HOME_HISTORY -> respond(chatId, messageId, buildNextScreenForHistory(chat));
            case HOME_BACK -> respond(chatId, messageId, orderService.prevScreen(CURRENT_SCREEN));
        }
        chatService.save(chat);
    }

    private CustomScreen buildNextScreenForCustomQuery() {
        List<CallbackButton> backButton = orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getText().equals(Navigational.BACK.getText()))
                .toList();
        return CustomScreen.builder()
                .text("Describe the desire dish, or count ingredients what you're have, or just type your mood...\n"
                        + "_Max 200 symbols, so the shorter the better :)_")
                .buttons(backButton)
                .build();
    }

    private void respondAsync(Chat chat, int messageId) {
        showSpinner(chat, messageId)
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(500)))
                .map(recipe -> buildNextScreenForRecipe(chat, recipe))
                .subscribe(screen -> respond(chat.getId(), messageId, screen));
    }

    private CustomScreen buildNextScreenForHistory(Chat chat) {
        List<CallbackButton> buttons = chat.getHistory().reversed().stream()
                .map(recipe -> CustomCallbackButton.builder()
                        .text(recipe.getTitle() + " - " + recipe.getProperties().cookingTime() + "\n")
                        .callbackData(HOME_HISTORY_CALLBACK_BASE + recipe.getId())
                        .build())
                .limit(42)
                .collect(Collectors.toList());
        orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getText().equals(Navigational.BACK.getText()))
                .findFirst()
                .ifPresent(buttons::add);
        return CustomScreen.builder()
                .text("Pick from the history:")
                .buttons(buttons)
                .build();
    }

    private CustomScreen buildNextScreenForRecipe(Chat chat, Recipe newRecipe) {
        chatService.setNewRecipe(chat, newRecipe);
        Screen nextScreenTemplate = orderService.nextScreen(CURRENT_SCREEN);
        return CustomScreen.builder()
                .buttons(nextScreenTemplate.getButtons())
                .text(newRecipe.toString())
                .build();
    }
}