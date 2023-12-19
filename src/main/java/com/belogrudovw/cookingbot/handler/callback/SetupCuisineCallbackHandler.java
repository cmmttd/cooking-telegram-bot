package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.buttons.CuisineButtons;
import com.belogrudovw.cookingbot.domain.properties.Cuisines;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreen;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.telegram.domain.Keyboard;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.belogrudovw.cookingbot.util.KeyboardUtil.buildDefaultKeyboard;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetupCuisineCallbackHandler implements CallbackHandler {

    private static final Screen SCREEN = DefaultScreen.SETUP_CUISINE;

    private final Storage<Long, Chat> chatStorage;
    private final OrderService orderService;
    private final ResponseService responseService;

    @Override
    public Set<String> getSupported() {
        return Arrays.stream(SCREEN.getButtons())
                .map(CallbackButton::getCallbackData)
                .collect(Collectors.toSet());
    }

    @Override
    public void handle(UserAction action) {
        log.debug("Cuisine handler called for: {}", action);
        long chatId = action.getChatId();
        UserAction.CallbackQuery callbackQuery = action.callbackQuery().orElseThrow();
        chatStorage.get(chatId)
                .map(chat -> mapToScreen(chat, callbackQuery))
                .ifPresent(nextScreen -> respond(nextScreen, chatId, callbackQuery));
    }

    private Screen mapToScreen(Chat chat, UserAction.CallbackQuery callbackQuery) {
        var button = CuisineButtons.valueOf(callbackQuery.data());
        if (CuisineButtons.SETUP_CUISINE_BACK == button) {
            return orderService.prevScreen(SCREEN);
        } else {
            chat.getProperty().setCuisine(Cuisines.from(button.getText()));
            chatStorage.save(chat);
            return orderService.nextScreen(SCREEN);
        }
    }

    private void respond(Screen nextScreen, long chatId, UserAction.CallbackQuery callbackQuery) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.editMessage(chatId, callbackQuery.message().messageId(), nextScreen.getText(), keyboard);
    }
}