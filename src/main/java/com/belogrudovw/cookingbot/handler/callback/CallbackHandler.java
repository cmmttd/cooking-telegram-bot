package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.handler.Handler;

import java.util.Set;

public interface CallbackHandler extends Handler {
    Set<String> getSupported();
}
