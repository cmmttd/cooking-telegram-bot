package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.DifficultyButtons;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.CallbackQuery;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetupDifficultyCallbackHandler extends AbstractCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.SETUP_DIFFICULTIES;

    Storage<Long, Chat> chatStorage;
    OrderService orderService;
    InteractionService interactionService;

    public SetupDifficultyCallbackHandler(Storage<Long, Chat> chatStorage, OrderService orderService,
                                          InteractionService interactionService) {
        super(chatStorage);
        this.chatStorage = chatStorage;
        this.orderService = orderService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, CallbackQuery callbackQuery) {
        var button = DifficultyButtons.valueOf(callbackQuery.data());
        Screen screen = switch (button) {
            case SETUP_DIFFICULTY_15,
                    SETUP_DIFFICULTY_30,
                    SETUP_DIFFICULTY_60,
                    SETUP_DIFFICULTY_INFINITY -> {
                chat.getRequestPreferences().setDifficulty(button.getDifficulties());
                chatStorage.save(chat);
                yield orderService.nextScreen(CURRENT_SCREEN);
            }
            case SETUP_DIFFICULTY_BACK -> orderService.prevScreen(CURRENT_SCREEN);
        };
        interactionService.showResponse(chat, callbackQuery.message().messageId(), screen);
    }
}