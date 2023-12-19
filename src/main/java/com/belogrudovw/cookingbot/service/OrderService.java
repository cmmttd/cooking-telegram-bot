package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.screen.Screen;

public interface OrderService {

    Screen prevScreen(Screen currentScreen);

    Screen nextScreen(Screen currentScreen);

    Screen getFirst();
}