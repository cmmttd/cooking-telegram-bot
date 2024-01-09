package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.LanguageButtons;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
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
public class SetupLangCallbackHandler extends AbstractCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.SETUP_LANG;

    ChatService chatService;
    OrderService orderService;

    public SetupLangCallbackHandler(ResponseService responseService, ChatService chatService, OrderService orderService) {
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
        LanguageButtons lang = LanguageButtons.valueOf(callbackQuery.data());
        chat.getRequestProperties().setLanguage(Languages.from(lang.getText()));
        chatService.save(chat);
        Screen screen = orderService.nextScreen(CURRENT_SCREEN);
        respond(chat.getId(), callbackQuery.message().messageId(), screen);
    }
}