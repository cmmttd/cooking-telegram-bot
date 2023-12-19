package com.belogrudovw.cookingbot.handler.callback;

import com.belogrudovw.cookingbot.handler.Handler;

public interface DynamicCallbackHandler extends Handler {
    String getPattern();
}
