package com.belogrudovw.cookingbot.handler.message;

import com.belogrudovw.cookingbot.handler.Handler;

import java.util.Set;

public interface MessageHandler extends Handler {
    Set<String> getSupportedMessageData();
}
