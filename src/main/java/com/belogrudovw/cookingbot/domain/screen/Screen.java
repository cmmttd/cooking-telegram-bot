package com.belogrudovw.cookingbot.domain.screen;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.lexic.StringToken;

import java.util.List;

public interface Screen {
    List<CallbackButton> getButtons();

    StringToken getTextToken();
}