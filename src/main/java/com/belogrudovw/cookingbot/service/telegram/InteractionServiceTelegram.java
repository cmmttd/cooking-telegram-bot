package com.belogrudovw.cookingbot.service.telegram;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.domain.telegram.Keyboard;
import com.belogrudovw.cookingbot.service.InteractionService;
import com.belogrudovw.cookingbot.service.ResponseService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildDefaultKeyboard;
import static com.belogrudovw.cookingbot.util.KeyboardBuilder.buildEmptyKeyboard;
import static com.belogrudovw.cookingbot.util.SpinnerBuilder.buildAwaitSpinner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractionServiceTelegram implements InteractionService {

    ResponseService responseService;

    @Override
    public void showSpinner(Chat chat) {
        responseService.sendMessage(chat.getId(), buildAwaitSpinner(chat), buildEmptyKeyboard());
    }

    @Override
    public void showSpinner(Chat chat, int messageId) {
        responseService.editMessage(chat.getId(), messageId, buildAwaitSpinner(chat), buildEmptyKeyboard());
    }

    @Override
    public void showResponse(Chat chat, Screen screen) {
        Languages language = chat.getRequestPreferences().getLanguage();
        Keyboard keyboard = buildDefaultKeyboard(screen.getButtons(), language);
        String text = screen.getTextToken().in(language);
        responseService.sendMessage(chat.getId(), text, keyboard);
    }

    @Override
    public void showResponse(Chat chat, long messageId, Screen screen) {
        Languages language = chat.getRequestPreferences().getLanguage();
        Keyboard keyboard = buildDefaultKeyboard(screen.getButtons(), language);
        String text = screen.getTextToken().in(language);
        responseService.editMessage(chat.getId(), messageId, text, keyboard);
    }
}