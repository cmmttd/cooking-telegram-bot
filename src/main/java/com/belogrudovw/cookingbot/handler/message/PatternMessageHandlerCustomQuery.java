package com.belogrudovw.cookingbot.handler.message;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.telegram.Message;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.handler.DefaultHandler;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PatternMessageHandlerCustomQuery implements PatternMessageHandler {

    static final DefaultScreens RESPONSE_SCREEN = DefaultScreens.SPIN_PICK_RECIPE;
    static final String CUSTOM_QUERY_PATTERN = "[[\\p{L}\\p{N}\\s!,.-]]{1,200}";

    DefaultHandler defaultHandler;
    ChatService chatService;
    Storage<Long, Chat> chatStorage;
    RecipeService recipeService;
    InteractionService interactionService;

    @Override
    public String getPattern() {
        return CUSTOM_QUERY_PATTERN;
    }

    @Override
    public void handle(UserAction action) {
        String text = action.message()
                .map(Message::text)
                .orElseThrow();
        log.info("Route '{}' to {} for user: {}", text, this.getClass().getSimpleName(), action.getUserName());
        long chatId = action.getChatId();
        chatStorage.findById(chatId)
                .map(chat -> {
                    if (chat.isAwaitCustomQuery()) {
                        chat.setAdditionalQuery(text);
                        respondAsync(chat);
                    } else {
                        log.warn("User {} tried custom query typing, but on wrong state", action.getUserName());
                        defaultHandler.handle(chat);
                    }
                    return chat;
                })
                .ifPresent(chatStorage::save);
    }

    private void respondAsync(Chat chat) {
        Mono.fromRunnable(() -> interactionService.showSpinner(chat))
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(500)))
                .map(recipe -> buildNextScreenForRecipe(chat, recipe))
                .subscribe(screen -> interactionService.showResponse(chat, screen));
    }

    private CustomScreen buildNextScreenForRecipe(Chat chat, Recipe newRecipe) {
        chatService.setNewRecipe(chat, newRecipe);
        return CustomScreen.builder()
                .buttons(RESPONSE_SCREEN.getButtons())
                .textToken(new SimpleStringToken(newRecipe.toFormattedString(chat.getRequestPreferences().getLanguage())))
                .build();
    }
}