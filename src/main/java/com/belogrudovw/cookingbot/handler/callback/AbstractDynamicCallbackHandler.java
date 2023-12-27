package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.service.ChatService;
import com.belogrudovw.cookingbot.service.ResponseService;

public abstract class AbstractDynamicCallbackHandler extends AbstractCallbackHandler implements DynamicCallbackHandler {

    AbstractDynamicCallbackHandler(ResponseService responseService, ChatService chatService) {
        super(responseService, chatService);
    }
}