package com.belogrudovw.cookingbot.config;

import com.belogrudovw.cookingbot.handler.callback.CallbackHandler;
import com.belogrudovw.cookingbot.handler.callback.PatternCallbackHandler;
import com.belogrudovw.cookingbot.handler.message.MessageHandler;
import com.belogrudovw.cookingbot.handler.message.PatternMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HandlerConfiguration {

    @Bean
    Map<String, CallbackHandler> callbackHandlersMap(List<CallbackHandler> callbackHandlers) {
        Map<String, CallbackHandler> callbackHandlersMap = new HashMap<>();
        for (CallbackHandler callbackHandler : callbackHandlers) {
            for (String callbackData : callbackHandler.getSupported()) {
                if (callbackHandlersMap.containsKey(callbackData)) {
                    String errorMessage = "Callback registration error. Found more than one handlers for callback data: %s"
                            .formatted(callbackData);
                    log.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
                callbackHandlersMap.put(callbackData, callbackHandler);
            }
        }
        return callbackHandlersMap;
    }

    @Bean
    Map<String, PatternCallbackHandler> dynamicCallbackHandlerMap(List<PatternCallbackHandler> callbackHandlers) {
        Map<String, PatternCallbackHandler> dynamicCallbackHandlerMap = new HashMap<>();
        for (PatternCallbackHandler callbackHandler : callbackHandlers) {
            String callbackDataPattern = callbackHandler.getPattern();
            if (dynamicCallbackHandlerMap.containsKey(callbackDataPattern)) {
                String errorMessage = "Dynamic callback registration error. Found more than one handlers for callback pattern: %s"
                        .formatted(callbackDataPattern);
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            dynamicCallbackHandlerMap.put(callbackDataPattern, callbackHandler);
        }
        return dynamicCallbackHandlerMap;
    }


    @Bean
    Map<String, MessageHandler> messageHandlersMap(List<MessageHandler> messageHandlers) {
        Map<String, MessageHandler> messageHandlersMap = new HashMap<>();
        for (MessageHandler messageHandler : messageHandlers) {
            for (String supportedMessage : messageHandler.getSupported()) {
                if (messageHandlersMap.containsKey(supportedMessage)) {
                    String errorMessage = "Message handler registration error. Found more than one handlers for message: %s"
                            .formatted(supportedMessage);
                    log.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
                messageHandlersMap.put(supportedMessage, messageHandler);
            }
        }
        return messageHandlersMap;
    }

    @Bean
    Map<String, PatternMessageHandler> dynamicMessageHandlerMap(List<PatternMessageHandler> messageHandlers) {
        Map<String, PatternMessageHandler> dynamicMessageHandlerMap = new HashMap<>();
        for (PatternMessageHandler messageHandler : messageHandlers) {
            String messageHandlerPattern = messageHandler.getPattern();
            if (dynamicMessageHandlerMap.containsKey(messageHandlerPattern)) {
                String errorMessage = "Dynamic message registration error. Found more than one handlers for message pattern: %s"
                        .formatted(messageHandlerPattern);
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            dynamicMessageHandlerMap.put(messageHandlerPattern, messageHandler);
        }
        return dynamicMessageHandlerMap;
    }
}