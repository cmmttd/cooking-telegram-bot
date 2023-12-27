package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.GenerationMode;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CustomCallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.HomeButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        if (chat.getProperty().isEmpty()) {
            String errorMessage = "Must have a non empty properties for step: %s. But properties there are: %s"
                    .formatted(CURRENT_SCREEN, chat.getProperty());
            throw new IllegalChatStateException(chatId, errorMessage);
        }
        var button = HomeButtons.valueOf(callbackQuery.data());
        chat.setPivotScreen(CURRENT_SCREEN);
        chatService.save(chat);
        Screen screen = switch (button) {
            case HOME_REQUEST_NEW -> buildNextScreenForRecipe(chat, GenerationMode.NEW, recipeService.requestNew(chat));
            case HOME_EXISTS -> buildNextScreenForRecipe(chat, GenerationMode.EXISTING, recipeService.getRandom(chat));
            case HOME_HISTORY -> buildNextScreenForHistory(chat);
            case HOME_BACK -> orderService.prevScreen(CURRENT_SCREEN);
        };
        respond(chatId, callbackQuery.message().messageId(), screen);
    }

    private CustomScreen buildNextScreenForHistory(Chat chat) {
        List<Recipe> history = chat.getHistory().reversed();
        List<CallbackButton> buttons = new ArrayList<>();
        for (Recipe recipe : history) {
            CustomCallbackButton newButton = CustomCallbackButton.builder()
                    .text(recipe.getTitle() + " - " + recipe.getCookingTime() + " min\n")
                    .callbackData(HOME_HISTORY_CALLBACK_BASE + recipe.getId())
                    .build();
            buttons.add(newButton);
        }
        orderService.nextScreen(CURRENT_SCREEN).getButtons().stream()
                .filter(button -> button.getText().equals("Back"))
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