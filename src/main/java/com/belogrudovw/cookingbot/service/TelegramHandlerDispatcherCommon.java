package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.handler.Handler;
import com.belogrudovw.cookingbot.handler.callback.CallbackHandler;
import com.belogrudovw.cookingbot.handler.callback.DynamicCallbackHandler;
import com.belogrudovw.cookingbot.handler.message.MessageHandler;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Map;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramHandlerDispatcherCommon implements TelegramHandlerDispatcher {

    @Resource
    private final Map<String, CallbackHandler> callbackHandlersMap;
    @Resource
    private final Map<String, DynamicCallbackHandler> dynamicCallbackHandlerMap;
    @Resource
    private final Map<String, MessageHandler> messageHandlersMap;
    private final Handler defaultHandler;
    private final Storage<Long, Chat> chatStorage;

    @PostConstruct
    void init() {
        log.debug("Callbacks handlers: {}", callbackHandlersMap.keySet());
        log.debug("Dynamic callbacks handlers: {}", dynamicCallbackHandlerMap.keySet());
        log.debug("Message handlers: {}", messageHandlersMap.keySet());
        log.debug("Default handler: {}", defaultHandler);
    }

    @Override
    public void dispatch(UserAction action) {
        findMessageHandler(action)
                .or(() -> findCallbackQueryHandler(action))
                .filter(handler -> validate(chatStorage.get(action.getChatId())))
                .ifPresentOrElse(handler -> handler.handle(action),
                        () -> defaultHandler.handle(action));
    }

    private boolean validate(Optional<Chat> chat) {
        return chat
                .filter(ch -> ch.getPivotScreen() != null)
                .isPresent();
    }

    private Optional<Handler> findMessageHandler(UserAction action) {
        return action.message()
                .map(UserAction.Message::text)
                .map(messageHandlersMap::get);
    }

    private Optional<Handler> findCallbackQueryHandler(UserAction action) {
        Optional<String> callbackData = action.callbackQuery()
                .map(UserAction.CallbackQuery::data);
        Optional<Handler> callbackHandler = callbackData
                .map(callbackHandlersMap::get);
        Optional<Handler> dynamicCallbackHandler = callbackData
                .flatMap(data -> dynamicCallbackHandlerMap.entrySet().stream()
                        .filter(pattern -> data.matches(pattern.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst());
        return callbackHandler
                .or(() -> dynamicCallbackHandler);
    }
}
