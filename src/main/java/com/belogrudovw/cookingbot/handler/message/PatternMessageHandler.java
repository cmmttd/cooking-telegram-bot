package com.belogrudovw.cookingbot.handler.message;

import java.util.Set;

public interface PatternMessageHandler extends MessageHandler {

    String getPattern();

    @Override
    default Set<String> getSupported() {
        return Set.of(getPattern());
    }
}
