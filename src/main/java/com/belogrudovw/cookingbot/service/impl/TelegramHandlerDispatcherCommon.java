package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.handler.Handler;
import com.belogrudovw.cookingbot.handler.callback.CallbackHandler;
import com.belogrudovw.cookingbot.handler.callback.DynamicCallbackHandler;
import com.belogrudovw.cookingbot.handler.message.MessageHandler;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.TelegramHandlerDispatcher;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

import java.util.Map;
import java.util.Optional;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramHandlerDispatcherCommon implements TelegramHandlerDispatcher {

    ChatService chatService;
    Handler defaultHandler;
    @Resource
    Map<String, CallbackHandler> callbackHandlersMap;
    @Resource
    Map<String, DynamicCallbackHandler> dynamicCallbackHandlerMap;
    @Resource
    Map<String, MessageHandler> messageHandlersMap;

    // TODO: 28/12/2023 Remove init section
    @PostConstruct
    void init() {
        log.debug("Callbacks handlers: {}", callbackHandlersMap.keySet());
        log.debug("Dynamic callbacks handlers: {}", dynamicCallbackHandlerMap.keySet());
        log.debug("Message handlers: {}", messageHandlersMap.keySet());
        log.debug("Default handler: {}", defaultHandler);
    }

    @Override
    public void dispatch(UserAction action) {
        Optional.<Handler>empty()
                .or(() -> findMessageHandler(action))
                .or(() -> findCallbackQueryHandler(action))
                .filter(handler -> chatService.isExists(action.getChatId()))
                .ifPresentOrElse(handler -> handler.handle(action),
                        () -> defaultHandler.handle(action));
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
                .flatMap(inputCallbackData -> dynamicCallbackHandlerMap.entrySet().stream()
                        .filter(patternHandler -> inputCallbackData.matches(patternHandler.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst());
        return callbackHandler
                .or(() -> dynamicCallbackHandler);
    }
}
