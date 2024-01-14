package com.belogrudovw.cookingbot.handler.callback;

import java.util.Set;

public interface PatternCallbackHandler extends CallbackHandler {
    String getPattern();

    @Override
    default Set<String> getSupported() {
        return Set.of(getPattern());
    }
}
