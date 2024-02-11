package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.Keyboard;
import com.belogrudovw.cookingbot.domain.telegram.TelegramResponse;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.storage.Storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildDefaultKeyboard;
import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildEmptyKeyboard;
import static com.belogrudovw.cookingbot.util.SpinnerBuilder.buildAwaitSpinner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractionServiceTelegram implements InteractionService {

    ResponseService responseService;
    Storage<Long, Chat> chatStorage;

    @Override
    public void showSpinner(Chat chat, int messageId) {
        responseService.editMessage(chat.getId(), messageId, buildAwaitSpinner(chat), buildEmptyKeyboard())
                .map(response -> updateLastUsedMessage(chat, response))
                .subscribe(chatStorage::save);
    }

    @Override
    public void showResponse(Chat chat, Screen screen) {
        Languages language = chat.getRequestPreferences().getLanguage();
        Keyboard keyboard = buildDefaultKeyboard(screen.getButtons(), language);
        String text = screen.getTextToken().in(language);
        responseService.sendMessage(chat.getId(), text, keyboard)
                .map(response -> updateLastUsedMessage(chat, response))
                .subscribe(chatStorage::save);
    }

    @Override
    public void showResponse(Chat chat, long messageId, Screen screen) {
        Languages language = chat.getRequestPreferences().getLanguage();
        Keyboard keyboard = buildDefaultKeyboard(screen.getButtons(), language);
        String text = screen.getTextToken().in(language);
        responseService.editMessage(chat.getId(), messageId, text, keyboard)
                .map(response -> updateLastUsedMessage(chat, response))
                .subscribe(chatStorage::save);
    }

    @Override
    public void showResponse(Chat chat, Screen screen, String imageId) {
        Languages language = chat.getRequestPreferences().getLanguage();
        Keyboard keyboard = buildDefaultKeyboard(screen.getButtons(), language);
        String text = screen.getTextToken().in(language);
        responseService.sendImage(chat.getId(), text, keyboard, imageId)
                .map(response -> updateLastUsedImageMessage(chat, response))
                .subscribe(chatStorage::save);
    }

    @Override
    public void showResponse(Chat chat, long messageId, Screen screen, String imageId) {
        Languages language = chat.getRequestPreferences().getLanguage();
        Keyboard keyboard = buildDefaultKeyboard(screen.getButtons(), language);
        String text = screen.getTextToken().in(language);
        responseService.editImage(chat.getId(), messageId, text, keyboard, imageId)
                .map(response -> updateLastUsedImageMessage(chat, response))
                .subscribe(chatStorage::save);
    }

    @Override
    public Mono<String> saveImage(byte[] file) {
        return responseService.saveImage(file)
                .map(photoSaveResponse -> photoSaveResponse.result().photo().get(0).fileId());
    }

    private Chat updateLastUsedMessage(Chat chat, TelegramResponse response) {
        chat.getLastUsedMessageId().set(response.result().messageId());
        return chat;
    }

    private Chat updateLastUsedImageMessage(Chat chat, TelegramResponse response) {
        chat.getLastUsedImageMessageId().set(response.result().messageId());
        return chat;
    }
}