package com.belogrudovw.cookingbot.domain.screen;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;

public interface Screen {
    CallbackButton[] getButtons();

    String getText();
}