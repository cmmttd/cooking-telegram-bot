package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.telegram.Message;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {

    Storage<Long, Chat> chatStorage;
    OrderService orderService;

    @Override
    public void setNewRecipe(Chat chat, Recipe recipe) {
        chat.setCurrentRecipe(recipe.getId());
        chat.setCookingProgress(0);
        chat.addLastRecipeToHistory();
        chat.getImageProgress().set(0);
        chatStorage.save(chat);
    }

    @Override
    public Chat createNewChat(long chatId, UserAction action) {
        Chat newChat = new Chat(chatId, action.getUserName());
        DefaultScreens firstScreen = orderService.getDefault();
        newChat.setPivotScreen(firstScreen);
        setIfUserBot(chatId, action, newChat);
        action.message()
                .map(Message::messageId)
                .ifPresent(id -> newChat.getLastUsedMessageId().set(id));
        newChat.setLastActiveTime(LocalDateTime.now());
        chatStorage.save(newChat);
        log.info("New user created with id: {}", chatId);
        return newChat;
    }

    private void setIfUserBot(long chatId, UserAction action, Chat newChat) {
        if (chatId < 0) {
            newChat.setUsername(action.toString());
        }
    }
}