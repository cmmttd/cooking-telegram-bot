package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CustomCallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.HomeButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.CallbackQuery;
import com.belogrudovw.cookingbot.exception.IllegalChatStateException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.EXPECT_CUSTOM_QUERY_TOKEN;
import static com.belogrudovw.cookingbot.lexic.MultilingualTokens.PICK_HISTORY_TOKEN;
import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeCallbackHandler extends AbstractCallbackHandler {

    public static final String HOME_HISTORY_CALLBACK_BASE = DefaultScreens.HOME.name() + "_";
    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.HOME;

    ChatService chatService;
    Storage<Long, Chat> chatStorage;
    Storage<UUID, Recipe> recipeStorage;
    OrderService orderService;
    RecipeService recipeService;
    InteractionService interactionService;

    // TODO: 29/01/2024 Get rid of overcomplexity
    public HomeCallbackHandler(ChatService chatService, Storage<Long, Chat> chatStorage, OrderService orderService,
                               RecipeService recipeService, Storage<UUID, Recipe> recipeStorage, InteractionService interactionService) {
        super(chatStorage);
        this.chatService = chatService;
        this.chatStorage = chatStorage;
        this.orderService = orderService;
        this.recipeService = recipeService;
        this.recipeStorage = recipeStorage;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, CallbackQuery callbackQuery) {
        RequestPreferences requestPreferences = chat.getRequestPreferences();
        if (requestPreferences.isEmpty()) {
            String errorMessage = "Must have a non empty preferences for step: %s. But properties there are: %s"
                    .formatted(CURRENT_SCREEN, requestPreferences);
            throw new IllegalChatStateException(chat, errorMessage);
        }
        var button = HomeButtons.valueOf(callbackQuery.data());
        chat.setPivotScreen(CURRENT_SCREEN);
        chat.setAwaitCustomQuery(false);
        final int messageId = callbackQuery.message().messageId();
        switch (button) {
            case HOME_CUSTOM -> {
                chat.setAwaitCustomQuery(true);
                interactionService.showResponse(chat, messageId, buildCustomQueryScreen());
            }
            case HOME_RANDOM -> respondRandomAsync(chat, messageId);
            case HOME_HISTORY -> interactionService.showResponse(chat, messageId, buildHistoryScreen(chat));
            case HOME_RESET_PREFERENCES -> interactionService.showResponse(chat, messageId, orderService.getDefault());
            case HOME_BACK -> interactionService.showResponse(chat, messageId, orderService.prevScreen(CURRENT_SCREEN));
        }
        chatStorage.save(chat);
    }

    private CustomScreen buildCustomQueryScreen() {
        List<CallbackButton> backButton = orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getTextToken().equals(BACK_TOKEN))
                .toList();
        return CustomScreen.builder()
                .textToken(EXPECT_CUSTOM_QUERY_TOKEN)
                .buttons(backButton)
                .build();
    }

    private void respondRandomAsync(Chat chat, int messageId) {
        Mono.fromRunnable(() -> interactionService.showSpinner(chat, messageId))
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(500)))
                .map(recipe -> buildSpinRecipeScreen(chat, recipe))
                .subscribe(screen -> interactionService.showResponse(chat, messageId, screen));
    }

    private CustomScreen buildSpinRecipeScreen(Chat chat, Recipe newRecipe) {
        chatService.setNewRecipe(chat, newRecipe);
        Screen nextScreenTemplate = orderService.nextScreen(CURRENT_SCREEN);
        return CustomScreen.builder()
                .buttons(nextScreenTemplate.getButtons())
                .textToken(new SimpleStringToken(newRecipe.toFormattedString(chat.getRequestPreferences().getLanguage())))
                .build();
    }

    private CustomScreen buildHistoryScreen(Chat chat) {
        var buttons = buildButtons(chat.getHistory());
        var backButton = orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getTextToken().equals(BACK_TOKEN));
        return CustomScreen.builder()
                .textToken(PICK_HISTORY_TOKEN)
                .buttons(Stream.concat(buttons, backButton).toList())
                .build();
    }

    private Stream<CallbackButton> buildButtons(Collection<UUID> recipeIds) {
        return recipeIds.stream()
                .map(recipeStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(recipe -> {
                    String text = recipe.getTitle() + " - " + recipe.getProperties().cookingTime();
                    return CustomCallbackButton.builder()
                            .textToken(new SimpleStringToken(text))
                            .callbackData(HOME_HISTORY_CALLBACK_BASE + recipe.getId())
                            .build();
                })
                .map(CallbackButton.class::cast);
    }
}