package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.ResponseService;

public abstract class AbstractPatternCallbackHandler extends AbstractCallbackHandler implements PatternCallbackHandler {

    AbstractPatternCallbackHandler(ResponseService responseService, ChatService chatService) {
        super(responseService, chatService);
    }
}