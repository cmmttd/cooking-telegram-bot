package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.telegram.CallbackQuery;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.exception.IllegalChatStateException;
import com.belogrudovw.cookingbot.storage.Storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractCallbackHandler implements CallbackHandler {

    Storage<Long, Chat> chatStorage;

    @Override
    public void handle(UserAction action) {
        chatStorage.findById(action.getChatId())
                .ifPresent(chat -> {
                    CallbackQuery callbackQuery = action.callbackQuery()
                            .orElseThrow(() -> new IllegalChatStateException(chat, "Callback data required for callback handlers"));
                    String userIdentification = chat.getId() + " - " + chat.getUsername();
                    log.info("Route {} to {} for user: {}", callbackQuery.data(), this.getClass().getSimpleName(), userIdentification);
                    handleCallback(chat, callbackQuery);
                });
    }
}