package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.GenerationMode;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
        chatService.save(chat);
        switch (button) {
            case HOME_RANDOM -> respondAsync(chat, callbackQuery);
            case HOME_HISTORY -> respond(chatId, callbackQuery.message().messageId(), buildNextScreenForHistory(chat));
            case HOME_BACK -> respond(chatId, callbackQuery.message().messageId(), orderService.prevScreen(CURRENT_SCREEN));
        }
    }

    private void respondAsync(Chat chat, UserAction.CallbackQuery callbackQuery) {
        showSpinner(chat, callbackQuery, chat.getRequestProperties())
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(500)))
                .map(recipe -> buildNextScreenForRecipe(chat, GenerationMode.EXISTING, recipe))
                .subscribe(screen -> respond(chat.getId(), callbackQuery.message().messageId(), screen));
    }

    private Mono<Void> showSpinner(Chat chat, UserAction.CallbackQuery callbackQuery, RequestProperties requestProperties) {
        String spinnerString = "Beautiful wait spinner on the way...%nPlease wait until generation finishes: %s %s %s %s"
                .formatted(
                        requestProperties.getLanguage().getText(),
                        requestProperties.getLightness().getText(),
                        requestProperties.getDifficulty().getText(),
                        requestProperties.getUnits().getText()
                );
        CustomScreen spinner = CustomScreen.builder().text(spinnerString).buttons(Collections.emptyList()).build();
        return Mono.fromRunnable(() -> respond(chat.getId(), callbackQuery.message().messageId(), spinner));
    }

    private CustomScreen buildNextScreenForHistory(Chat chat) {
        List<Recipe> history = chat.getHistory().reversed();
        List<CallbackButton> buttons = new ArrayList<>();
        for (Recipe recipe : history) {
            CustomCallbackButton newButton = CustomCallbackButton.builder()
                    .text(recipe.getTitle() + " - " + recipe.getProperties().cookingTime() + "\n")
                    .callbackData(HOME_HISTORY_CALLBACK_BASE + recipe.getId())
                    .build();
            buttons.add(newButton);
        }
        orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getText().equals(Navigational.BACK.getText()))
                .findFirst()
                .ifPresent(buttons::add);
        return CustomScreen.builder()
                .text("Pick from the history:")
                .buttons(buttons)
                .build();
    }

    private CustomScreen buildNextScreenForRecipe(Chat chat, GenerationMode mode, Recipe newRecipe) {
        chat.setMode(mode);
        chatService.setNewRecipe(chat, newRecipe);
        Screen nextScreenTemplate = orderService.nextScreen(CURRENT_SCREEN);
        return CustomScreen.builder()
                .buttons(nextScreenTemplate.getButtons())
                .text(newRecipe.toString())
                .build();
    }
}