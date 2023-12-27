package com.belogrudovw.cookingbot.handler;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.OrderService;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.storage.Storage;
import com.belogrudovw.cookingbot.telegram.domain.UserAction;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.belogrudovw.cookingbot.util.KeyboardUtil.buildDefaultKeyboard;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultHandler implements Handler {

    private final Storage<Long, Chat> chatStorage;
    private final OrderService orderService;
    private final ResponseService responseService;

    @Override
    public void handle(UserAction action) {
        log.debug("Default handler called for: {}", action);
        long chatId = action.getChatId();
        chatStorage.get(chatId)
                .map(Chat::getPivotScreen)
                .or(() -> createNewChat(chatId))
                .ifPresent(screen -> respond(screen, chatId));
    }

    private void respond(Screen screen, long chatId) {
        responseService.sendMessage(chatId, screen.getText(), buildDefaultKeyboard(screen.getButtons()));
    }

    // TODO: 20/12/2023 Move it into chat service
    private Optional<Screen> createNewChat(long chatId) {
        Chat newChat = new Chat(chatId);
        Screen firstScreen = orderService.getFirst();
        newChat.setPivotScreen(firstScreen);
        chatStorage.save(newChat);
        log.info("New user saved: {}", chatId);
        return Optional.of(firstScreen);
    }
}
