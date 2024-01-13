package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.RequestProperties;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.domain.telegram.Keyboard;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

import java.util.Collections;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildDefaultKeyboard;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractCallbackHandler implements CallbackHandler {

    ResponseService responseService;
    ChatService chatService;

    @Override
    public void handle(UserAction action) {
        UserAction.CallbackQuery callbackQuery = action.callbackQuery()
                .orElseThrow(() -> new IllegalChatStateException(action.getChatId(), "Callback data required for callback handlers"));
        String userIdentification = action.getChatId() + " - " + action.getUserName();
        log.info("Route {} to {} for user: {}", callbackQuery.data(), this.getClass().getSimpleName(), userIdentification);
        Chat chat = chatService.findById(action.getChatId());
        handleCallback(chat, callbackQuery);
    }

    // TODO: 13/01/2024 Move it to somewhere
    public Mono<Void> showSpinner(Chat chat, UserAction.CallbackQuery callbackQuery, RequestProperties requestProperties) {
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

    public void respond(long chatId, long messageId, Screen nextScreen) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.editMessage(chatId, messageId, nextScreen.getText(), keyboard);
    }

    public void respond(long chatId, Screen nextScreen) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.sendMessage(chatId, nextScreen.getText(), keyboard);
    }
}