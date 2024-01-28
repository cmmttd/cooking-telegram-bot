package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;

public interface OrderService {

    DefaultScreens prevScreen(DefaultScreens currentScreen);

    DefaultScreens nextScreen(DefaultScreens currentScreen);

    DefaultScreens getDefault();
}