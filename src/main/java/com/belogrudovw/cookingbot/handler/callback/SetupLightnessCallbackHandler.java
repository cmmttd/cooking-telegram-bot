package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.LightnessButtons;
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
public class SetupLightnessCallbackHandler extends AbstractCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.SETUP_LIGHTNESS;

    OrderService orderService;
    InteractionService interactionService;

    public SetupLightnessCallbackHandler(Storage<Long, Chat> chatStorage, OrderService orderService,
                                         InteractionService interactionService) {
        super(chatStorage);
        this.orderService = orderService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, CallbackQuery callbackQuery) {
        var button = LightnessButtons.valueOf(callbackQuery.data());
        Screen screen = switch (button) {
            case SETUP_LIGHTNESS_LIGHT,
                    SETUP_LIGHTNESS_MODERATE,
                    SETUP_LIGHTNESS_HEAVY,
                    SETUP_LIGHTNESS_ANY -> {
                chat.getRequestPreferences().setLightness(button.getLightness());
                yield orderService.nextScreen(CURRENT_SCREEN);
            }
            case SETUP_LIGHTNESS_BACK -> orderService.prevScreen(CURRENT_SCREEN);
        };
        interactionService.showResponse(chat, callbackQuery.message().messageId(), screen);
    }
}