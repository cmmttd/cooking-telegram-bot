package com.belogrudovw.cookingbot.handler;

import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.ResponseService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildDefaultKeyboard;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultHandler implements Handler {

    ChatService chatService;
    ResponseService responseService;

    @Override
    public void handle(UserAction action) {
        log.info("Default handler called for: {}", action.toString().replaceAll("\n", ""));
        handle(action.getChatId());
    }

    @Override
    public void handle(long chatId) {
        respond(chatService.findById(chatId).getPivotScreen(), chatId);
    }

    private void respond(Screen screen, long chatId) {
        responseService.sendMessage(chatId, screen.getText(), buildDefaultKeyboard(screen.getButtons()));
    }
}