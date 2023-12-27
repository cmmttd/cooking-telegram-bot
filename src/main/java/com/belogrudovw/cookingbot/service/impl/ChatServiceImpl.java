package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.util.Optional;

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
    public boolean isExists(long chatId) {
        return chatStorage.contains(chatId);
    }

    @Override
    public Chat findById(long chatId) {
        return chatStorage.get(chatId)
                .orElseGet(() -> createNewChat(chatId));
    }

    @Override
    public void save(Chat chat) {
        chatStorage.save(chat);
    }

    @Override
    public Optional<Recipe.CookingStep> nextRecipeStep(Chat chat) {
        Recipe recipe = chat.getCurrentRecipe();
        int cookingProgress = chat.getCookingProgress();
        if (recipe.getSteps().size() > cookingProgress) {
            Recipe.CookingStep nextStep = recipe.getSteps().get(cookingProgress);
            chat.setCookingProgress(cookingProgress + 1);
            chatStorage.save(chat);
            return Optional.of(nextStep);
        }
        return Optional.empty();
    }

    @Override
    public void setNewRecipe(Chat chat, Recipe recipe) {
        chat.setCurrentRecipe(recipe);
        chat.setCookingProgress(0);
        chat.addLastRecipeToHistory();
        chatStorage.save(chat);
    }

    private Chat createNewChat(long chatId) {
        Chat newChat = new Chat(chatId);
        Screen firstScreen = orderService.getDefault();
        newChat.setPivotScreen(firstScreen);
        chatStorage.save(newChat);
        log.info("New user saved: {}", chatId);
        return newChat;
    }
}
