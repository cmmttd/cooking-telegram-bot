package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.MeasurementUnitButtons;
import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Set;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetupUnitsCallbackHandler extends AbstractCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.SETUP_UNITS;

    ChatService chatService;
    OrderService orderService;

    public SetupUnitsCallbackHandler(ResponseService responseService, ChatService chatService, OrderService orderService) {
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
        var button = MeasurementUnitButtons.valueOf(callbackQuery.data());
        Screen screen = switch (button) {
            case SETUP_UNITS_IMPERIAL,
                    SETUP_UNITS_METRIC -> {
                chat.getProperty().setUnits(MeasurementUnits.from(button.getText()));
                chatService.save(chat);
                yield orderService.nextScreen(CURRENT_SCREEN);
            }
            case SETUP_UNITS_BACK -> orderService.prevScreen(CURRENT_SCREEN);
        };
        respond(chat.getId(), callbackQuery.message().messageId(), screen);
    }
}