package com.belogrudovw.cookingbot.handler.message;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.Keyboard;
import com.belogrudovw.cookingbot.domain.telegram.UserAction;
import com.belogrudovw.cookingbot.handler.DefaultHandler;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.service.ResponseService;

import java.time.Duration;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildDefaultKeyboard;


@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PatternMessageHandlerCustomQuery implements PatternMessageHandler {

    static final DefaultScreens RESPONSE_SCREEN = DefaultScreens.SPIN_PICK_RECIPE;
    static final String CUSTOM_QUERY_PATTERN = "[[\\p{L}\\p{N}\\s!,.-]]{1,200}";

    ChatService chatService;
    ResponseService responseService;
    DefaultHandler defaultHandler;
    RecipeService recipeService;

    @Override
    public String getPattern() {
        return CUSTOM_QUERY_PATTERN;
    }

    @Override
    public void handle(UserAction action) {
        String text = action.message()
                .map(UserAction.Message::text)
                .orElseThrow();
        log.info("Route '{}' to {} for user: {}", text, this.getClass().getSimpleName(), action.getUserName());
        long chatId = action.getChatId();
        Chat chat = chatService.findById(chatId);
        if (chat.isAwaitCustomQuery()) {
            chat.setAdditionalQuery(text);
            respondAsync(chat);
        } else {
            log.warn("User {} tried custom query typing, but on wrong state", action.getUserName());
            defaultHandler.handle(action);
        }
        chatService.save(chat);
    }

    private void respondAsync(Chat chat) {
        showSpinner(chat)
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(500)))
                .map(recipe -> buildNextScreenForRecipe(chat, recipe))
                .subscribe(screen -> respond(chat.getId(), screen));
    }

    public Mono<Void> showSpinner(Chat chat) {
        String spinnerString = "Beautiful wait spinner on the way...%nPlease wait until generation finishes: %s %s %s %s"
                .formatted(
                        chat.getRequestProperties().getLanguage().getText(),
                        chat.getRequestProperties().getLightness().getText(),
                        chat.getRequestProperties().getDifficulty().getText(),
                        chat.getRequestProperties().getUnits().getText()
                );
        if (chat.isAwaitCustomQuery() && chat.getAdditionalQuery() != null) {
            spinnerString += " " + chat.getAdditionalQuery();
        }
        CustomScreen spinner = CustomScreen.builder().text(spinnerString).buttons(Collections.emptyList()).build();
        return Mono.fromRunnable(() -> respond(chat.getId(), spinner));
    }

    private CustomScreen buildNextScreenForRecipe(Chat chat, Recipe newRecipe) {
        chatService.setNewRecipe(chat, newRecipe);
        return CustomScreen.builder()
                .buttons(RESPONSE_SCREEN.getButtons())
                .text(newRecipe.toString())
                .build();
    }

    public void respond(long chatId, long messageId, Screen nextScreen) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.editMessage(chatId, messageId, nextScreen.getText(), keyboard);
    }

    public void respond(long chatId, Screen nextScreen) {
        Keyboard keyboard = buildDefaultKeyboard(nextScreen.getButtons());
        responseService.sendMessage(chatId, nextScreen.getText(), keyboard);
    }
}