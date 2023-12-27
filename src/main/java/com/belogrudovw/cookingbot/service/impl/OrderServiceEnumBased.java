package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.screen.Screen;
import com.belogrudovw.cookingbot.service.OrderService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.COOKING;
import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.HOME;
import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.SETUP_DIFFICULTIES;
import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.SETUP_LANG;
import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.SETUP_LIGHTNESS;
import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.SETUP_UNITS;
import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.SPIN_PICK_RECIPE;

@Service
public class OrderServiceEnumBased implements OrderService {
    private static final Map<Screen, LinkedScreen> order;

    static {
        order = new HashMap<>();
        order.put(SETUP_LANG, new LinkedScreen(SETUP_LANG, SETUP_UNITS));
        order.put(SETUP_UNITS, new LinkedScreen(SETUP_LANG, SETUP_LIGHTNESS));
        order.put(SETUP_LIGHTNESS, new LinkedScreen(SETUP_UNITS, SETUP_DIFFICULTIES));
        order.put(SETUP_DIFFICULTIES, new LinkedScreen(SETUP_LIGHTNESS, HOME));
        order.put(HOME, new LinkedScreen(SETUP_DIFFICULTIES, SPIN_PICK_RECIPE));
        order.put(SPIN_PICK_RECIPE, new LinkedScreen(HOME, COOKING));
        order.put(COOKING, new LinkedScreen(HOME, HOME));
    }

    @Override
    public Screen prevScreen(Screen currentScreen) {
        return order.get(currentScreen).prev();
    }

    @Override
    public Screen nextScreen(Screen currentScreen) {
        return order.get(currentScreen).next();
    }

    @Override
    public Screen getDefault() {
        return SETUP_LANG;
    }

    private record LinkedScreen(Screen prev, Screen next) {
    }
}