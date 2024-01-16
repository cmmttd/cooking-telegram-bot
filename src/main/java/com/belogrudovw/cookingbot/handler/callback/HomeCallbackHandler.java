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
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    static final Screen CURRENT_SCREEN = DefaultScreens.HOME;

    ChatService chatService;
    OrderService orderService;
    RecipeService recipeService;
    InteractionService interactionService;

    public HomeCallbackHandler(ChatService chatService, OrderService orderService, RecipeService recipeService,
                               InteractionService interactionService) {
        super(chatService);
        this.chatService = chatService;
        this.orderService = orderService;
        this.recipeService = recipeService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        RequestPreferences requestPreferences = chat.getRequestPreferences();
        if (requestPreferences.isEmpty()) {
            String errorMessage = "Must have a non empty properties for step: %s. But properties there are: %s"
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
            case HOME_RANDOM -> respondAsync(chat, messageId);
            case HOME_HISTORY -> interactionService.showResponse(chat, messageId, buildHistoryScreen(chat));
            case HOME_BACK -> interactionService.showResponse(chat, messageId, orderService.prevScreen(CURRENT_SCREEN));
        }
        chatService.save(chat);
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

    private void respondAsync(Chat chat, int messageId) {
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
        List<CallbackButton> buttons = buildButtons(chat.getHistory().stream());
        orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getTextToken().equals(BACK_TOKEN))
                .findFirst()
                .ifPresent(buttons::add);
        return CustomScreen.builder()
                .textToken(PICK_HISTORY_TOKEN)
                .buttons(buttons)
                .build();
    }

    private static List<CallbackButton> buildButtons(Stream<Recipe> recipeStream) {
        return recipeStream
                .map(recipe -> {
                    String text = recipe.getTitle() + " - " + recipe.getProperties().cookingTime();
                    return CustomCallbackButton.builder()
                            .textToken(new SimpleStringToken(text))
                            .callbackData(HOME_HISTORY_CALLBACK_BASE + recipe.getId())
                            .build();
                })
                .collect(Collectors.toList());
    }
}