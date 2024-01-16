package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.error.IllegalChatStateException;
import com.belogrudovw.cookingbot.service.ChatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractCallbackHandler implements CallbackHandler {

    ChatService chatService;

    @Override
    public void handle(UserAction action) {
        Chat chat = chatService.findById(action.getChatId());
        UserAction.CallbackQuery callbackQuery = action.callbackQuery()
                .orElseThrow(() -> new IllegalChatStateException(chat, "Callback data required for callback handlers"));
        String userIdentification = action.getChatId() + " - " + action.getUserName();
        log.info("Route {} to {} for user: {}", callbackQuery.data(), this.getClass().getSimpleName(), userIdentification);
        handleCallback(chat, callbackQuery);
    }
}