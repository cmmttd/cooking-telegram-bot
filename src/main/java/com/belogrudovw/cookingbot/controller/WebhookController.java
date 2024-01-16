package com.belogrudovw.cookingbot.controller;

import com.belogrudovw.cookingbot.service.HandlerDispatcher;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

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

    private final HandlerDispatcher handlerDispatcher;

    @PostMapping("/webhook/")
    public boolean post(@Valid @RequestBody UserAction action) {
        handlerDispatcher.dispatch(action);
        return true;
    }
}