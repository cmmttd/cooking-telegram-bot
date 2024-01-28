package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.domain.screen.DefaultScreens;
import com.belogrudovw.cookingbot.service.OrderService;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import static com.belogrudovw.cookingbot.domain.screen.DefaultScreens.*;

@Service
public class OrderServiceEnumBased implements OrderService {
    private static final Map<DefaultScreens, LinkedScreen> order;

    static {
        order = new EnumMap<>(DefaultScreens.class);
        order.put(SETUP_LANG, new LinkedScreen(SETUP_LANG, SETUP_UNITS));
        order.put(SETUP_UNITS, new LinkedScreen(SETUP_LANG, SETUP_LIGHTNESS));
        order.put(SETUP_LIGHTNESS, new LinkedScreen(SETUP_UNITS, SETUP_DIFFICULTIES));
        order.put(SETUP_DIFFICULTIES, new LinkedScreen(SETUP_LIGHTNESS, HOME));
        order.put(HOME, new LinkedScreen(SETUP_DIFFICULTIES, SPIN_PICK_RECIPE));
        order.put(SPIN_PICK_RECIPE, new LinkedScreen(HOME, COOKING));
        order.put(COOKING, new LinkedScreen(HOME, HOME));
    }

    @Override
    public DefaultScreens prevScreen(DefaultScreens currentScreen) {
        return order.get(currentScreen).prev();
    }

    @Override
    public DefaultScreens nextScreen(DefaultScreens currentScreen) {
        return order.get(currentScreen).next();
    }

    @Override
    public DefaultScreens getDefault() {
        return SETUP_LANG;
    }

    private record LinkedScreen(DefaultScreens prev, DefaultScreens next) {
    }
}