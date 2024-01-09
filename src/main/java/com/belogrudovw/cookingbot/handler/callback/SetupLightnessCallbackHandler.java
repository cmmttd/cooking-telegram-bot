package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.LightnessButtons;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetupLightnessCallbackHandler extends AbstractCallbackHandler {

    static final Screen CURRENT_SCREEN = DefaultScreens.SETUP_LIGHTNESS;

    ChatService chatService;
    OrderService orderService;

    public SetupLightnessCallbackHandler(ResponseService responseService, ChatService chatService, OrderService orderService) {
        super(responseService, chatService);
        this.chatService = chatService;
        this.orderService = orderService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, UserAction.CallbackQuery callbackQuery) {
        var button = LightnessButtons.valueOf(callbackQuery.data());
        Screen screen = switch (button) {
            case SETUP_LIGHTNESS_LIGHT,
                    SETUP_LIGHTNESS_MODERATE,
                    SETUP_LIGHTNESS_HEAVY,
                    SETUP_LIGHTNESS_ANY -> {
                chat.getRequestProperties().setLightness(Lightness.from(button.getText()));
                chatService.save(chat);
                yield orderService.nextScreen(CURRENT_SCREEN);
            }
            case SETUP_LIGHTNESS_BACK -> orderService.prevScreen(CURRENT_SCREEN);
        };
        respond(chat.getId(), callbackQuery.message().messageId(), screen);
    }
}