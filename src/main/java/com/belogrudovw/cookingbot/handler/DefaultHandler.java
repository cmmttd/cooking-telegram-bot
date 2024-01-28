package com.belogrudovw.cookingbot.handler;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.telegram.From;
import com.belogrudovw.cookingbot.domain.telegram.Message;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.storage.Storage;

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

    Storage<Long, Chat> chatStorage;
    ChatService chatService;
    InteractionService interactionService;

    @Override
    public void handle(UserAction action) {
        log.info("Default handler called for: {}", action.toString().replaceAll("\n", ""));
        long chatId = action.getChatId();
        Chat chat = chatStorage.findById(chatId)
                .orElseGet(() -> chatService.createNewChat(action));
        if (chat.getRequestPreferences().getLanguage() == null) {
            Languages language = action.message()
                    .map(Message::from)
                    .map(From::languageCode)
                    .map(Languages::from)
                    .orElse(Languages.EN);
            chat.getRequestPreferences().setLanguage(language);
            chatStorage.save(chat);
        }
        handle(chat);
    }

    @Override
    public void handle(Chat chat) {
        interactionService.showResponse(chat, chat.getPivotScreen());
    }
}