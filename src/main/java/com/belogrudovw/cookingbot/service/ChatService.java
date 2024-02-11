package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;

public interface ChatService {

    void setNewRecipe(Chat chat, Recipe recipe);

    Chat createNewChat(long chatId, UserAction action);
}