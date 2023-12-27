package com.belogrudovw.cookingbot.controller;

import com.belogrudovw.cookingbot.service.TelegramHandlerDispatcher;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final TelegramHandlerDispatcher telegramHandlerDispatcher;

    @PostMapping("/webhook/")
    public boolean post(@Valid @RequestBody UserAction action) {
        telegramHandlerDispatcher.dispatch(action);
        return true;
    }
}