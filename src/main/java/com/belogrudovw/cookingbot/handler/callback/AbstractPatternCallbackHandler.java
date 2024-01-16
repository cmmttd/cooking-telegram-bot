package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.service.ChatService;

public abstract class AbstractPatternCallbackHandler extends AbstractCallbackHandler implements PatternCallbackHandler {

    AbstractPatternCallbackHandler(ChatService chatService) {
        super(chatService);
    }
}