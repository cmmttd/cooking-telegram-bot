package com.belogrudovw.cookingbot.handler.message;

import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.handler.Handler;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartMessageHandler implements MessageHandler {

    private final Handler defaultHandler;

    @Override
    public Set<String> getSupported() {
        return Set.of("start", "START", "Start", "/start", "/START", "/Start");
    }

    @Override
    public void handle(UserAction action) {
        log.info("Start message handler");
        defaultHandler.handle(action);
    }
}