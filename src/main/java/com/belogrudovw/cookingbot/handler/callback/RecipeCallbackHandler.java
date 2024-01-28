package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.buttons.RecipeButtons;
import com.belogrudovw.cookingbot.domain.screen.CustomScreen;
import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.domain.telegram.CallbackQuery;
import com.belogrudovw.cookingbot.exception.IllegalChatStateException;
import com.belogrudovw.cookingbot.lexic.SimpleStringToken;
import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.CookingScheduleService;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.RecipeService;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeCallbackHandler extends AbstractCallbackHandler {

    static final DefaultScreens CURRENT_SCREEN = DefaultScreens.SPIN_PICK_RECIPE;

    ChatService chatService;
    OrderService orderService;
    RecipeService recipeService;
    Storage<UUID, Recipe> recipeStorage;
    CookingScheduleService cookingScheduleService;
    InteractionService interactionService;

    // TODO: 29/01/2024 Get rid of overcomplexity
    public RecipeCallbackHandler(ChatService chatService, Storage<Long, Chat> chatStorage, OrderService orderService,
                                 RecipeService recipeService, Storage<UUID, Recipe> recipeStorage,
                                 CookingScheduleService cookingScheduleService, InteractionService interactionService) {
        super(chatStorage);
        this.chatService = chatService;
        this.orderService = orderService;
        this.recipeStorage = recipeStorage;
        this.recipeService = recipeService;
        this.cookingScheduleService = cookingScheduleService;
        this.interactionService = interactionService;
    }

    @Override
    public Set<String> getSupported() {
        return setOfCallbackDataFrom(CURRENT_SCREEN);
    }

    @Override
    public void handleCallback(Chat chat, CallbackQuery callbackQuery) {
        if (chat.getCurrentRecipe() == null) {
            throw new IllegalChatStateException(chat, "Recipe must be not null on the step: " + CURRENT_SCREEN);
        }
        var button = RecipeButtons.valueOf(callbackQuery.data());
        final int messageId = callbackQuery.message().messageId();
        switch (button) {
            case RECIPE_START -> {
                hideButtons(chat, messageId);
                cookingScheduleService.scheduleNexStep(chat);
            }
            case RECIPE_SPIN -> respondNewRecipe(chat, messageId);
//            case RECIPE_CALORIC -> respondCaloric(chat, messageId);
//            case RECIPE_IMAGE -> respondImage(chat, messageId);
            case RECIPE_BACK -> interactionService.showResponse(chat, messageId, orderService.prevScreen(CURRENT_SCREEN));
        }
    }

    private void hideButtons(Chat chat, int messageId) {
        recipeStorage.findById(chat.getCurrentRecipe())
                .map(recipe -> recipe.toFormattedString(chat.getRequestPreferences().getLanguage()))
                .map(recipeString -> CustomScreen.builder()
                        .buttons(Collections.emptyList())
                        .textToken(new SimpleStringToken(recipeString))
                        .build())
                .ifPresent(screen -> interactionService.showResponse(chat, messageId, screen));
    }

    private void respondImage(Chat chat, int messageId) {
        // TODO: 25/01/2024 Implement
        // 1. call sd for image
        // 2. respond image to recipe chat
        //        FileSystemResource photo = new FileSystemResource("/Users/slb/Downloads/qwer.png");
        //        client.post()
        //                .uri("/sendPhoto")
        //                .contentType(MediaType.MULTIPART_FORM_DATA)
        //                .body(BodyInserters
        //                        .fromMultipartData("chat_id", "-1002012962538")
        //                        .with("photo", photo)
        //                )
        //                .exchangeToMono(resp -> resp.bodyToMono(PhotoSaveResponse.class))
        //                .subscribe(System.out::println);
        // 3. respond with photo_id to original chat
    }

    private void respondCaloric(Chat chat, int messageId) {
        // TODO: 25/01/2024 Implement
        // 1. call gpt for caloric
        // 2. respond asynchronously
    }

    private void respondNewRecipe(Chat chat, int messageId) {
        Mono.fromRunnable(() -> interactionService.showSpinner(chat, messageId))
                .then(recipeService.getRandom(chat)
                        .delaySubscription(Duration.ofMillis(300)))
                .map(recipe -> {
                    chatService.setNewRecipe(chat, recipe);
                    return recipe;
                })
                .map(recipe -> CustomScreen.builder()
                        .buttons(CURRENT_SCREEN.getButtons())
                        .textToken(new SimpleStringToken(recipe.toFormattedString(chat.getRequestPreferences().getLanguage())))
                        .build())
                .subscribe(screen -> interactionService.showResponse(chat, messageId, screen));
    }
}