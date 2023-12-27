package com.belogrudovw.cookingbot.domain.screen;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;

import java.util.List;

public interface Screen {
    List<CallbackButton> getButtons();

    String getText();
}