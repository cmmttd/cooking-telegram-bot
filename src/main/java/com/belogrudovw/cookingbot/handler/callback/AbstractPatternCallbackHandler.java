package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.storage.Storage;

public abstract class AbstractPatternCallbackHandler extends AbstractCallbackHandler implements PatternCallbackHandler {

    AbstractPatternCallbackHandler(Storage<Long, Chat> chatStorage) {
        super(chatStorage);
    }
}