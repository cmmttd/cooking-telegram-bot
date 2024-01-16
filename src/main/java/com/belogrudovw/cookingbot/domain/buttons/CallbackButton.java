package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

public interface CallbackButton {
    StringToken getTextToken();

    String getCallbackData();
}
