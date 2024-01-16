package com.belogrudovw.cookingbot.handler;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.InteractionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultHandler implements Handler {

    ChatService chatService;
    InteractionService interactionService;

    @Override
    public void handle(UserAction action) {
        log.info("Default handler called for: {}", action.toString().replaceAll("\n", ""));
        Chat chat = chatService.findById(action.getChatId());
        if (chat.getRequestPreferences().getLanguage() == null) {
            chat.getRequestPreferences().setLanguage(action.message()
                    .map(UserAction.Message::from)
                    .map(UserAction.From::languageCode)
                    .map(Languages::from)
                    .orElse(Languages.EN));
        }
        handle(chat);
    }

    @Override
    public void handle(Chat chat) {
        interactionService.showResponse(chat, chat.getPivotScreen());
    }
}