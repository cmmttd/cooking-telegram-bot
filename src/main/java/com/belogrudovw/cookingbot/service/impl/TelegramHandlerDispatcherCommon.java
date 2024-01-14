package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.handler.Handler;
import com.belogrudovw.cookingbot.handler.callback.CallbackHandler;
import com.belogrudovw.cookingbot.handler.callback.PatternCallbackHandler;
import com.belogrudovw.cookingbot.handler.message.MessageHandler;
import com.belogrudovw.cookingbot.handler.message.PatternMessageHandler;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.TelegramHandlerDispatcher;

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
    Map<String, PatternCallbackHandler> dynamicCallbackHandlerMap;
    @Resource
    Map<String, MessageHandler> messageHandlersMap;
    @Resource
    Map<String, PatternMessageHandler> dynamicMessageHandlerMap;

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
        String flattenedActionString = action.toString().replaceAll("\n", " ");
        log.debug("Action received: {}", flattenedActionString);
        // TODO: 10/01/2024 wrap all handling by try catch and rethrow exception with user action
        Optional.<Handler>empty()
                .or(() -> findMessageHandler(action))
                .or(() -> findCallbackQueryHandler(action))
                .filter(handler -> chatService.isExists(action.getChatId()))
                .ifPresentOrElse(handler -> handler.handle(action),
                        () -> defaultHandler.handle(action));
    }

    private Optional<Handler> findMessageHandler(UserAction action) {
        Optional<String> messageText = action.message()
                .map(UserAction.Message::text);
        Optional<Handler> patternMessageHandler = messageText
                .flatMap(text -> dynamicMessageHandlerMap.entrySet().stream()
                        .filter(pattern -> text.matches(pattern.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst());
        Optional<Handler> messageHandler = messageText
                .map(messageHandlersMap::get);
        return patternMessageHandler
                .or(() -> messageHandler);
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
