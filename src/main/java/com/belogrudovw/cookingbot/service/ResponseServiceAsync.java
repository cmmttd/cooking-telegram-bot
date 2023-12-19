package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.telegram.domain.Keyboard;
import com.belogrudovw.cookingbot.util.CustomUriBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseServiceAsync implements ResponseService {

    private final WebClient client;

    @Override
    public void sendMessage(long chatId, String text, Keyboard keyboard) {
        String uri = CustomUriBuilder.builder()
                .path("/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", text)
                .queryParam("parse_mode", "MarkdownV2")
                .queryParam("reply_markup", keyboard.toString())
                .build();
        client.post()
                .uri(uri)
                .exchangeToMono(resp -> resp.bodyToMono(String.class))
                .subscribe(s -> log.debug(">> Response: {}", s));
    }

    @Override
    public void editMessage(long chatId, long messageId, String text, Keyboard keyboard) {
        String uri = CustomUriBuilder.builder()
                .path("/editMessageText")
                .queryParam("chat_id", chatId)
                .queryParam("message_id", messageId)
                .queryParam("text", text)
                .queryParam("parse_mode", "MarkdownV2")
                .queryParam("reply_markup", keyboard.toString())
                .build();
        client.post()
                .uri(uri)
                .exchangeToMono(resp -> resp.bodyToMono(String.class))
                .subscribe(s -> log.debug(">> Response: {}", s));
    }
}