package com.belogrudovw.cookingbot.util;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.telegram.domain.Button;
import com.belogrudovw.cookingbot.telegram.domain.Keyboard;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

// TODO: 15/12/2023 Remove this class
@UtilityClass
public final class KeyboardUtil {
    public static final int ROW_LENGTH_LIMIT = 15;

    public static Keyboard buildDefaultKeyboard(CallbackButton[] values) {
        List<List<Button>> keyboard = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        int lineLen = 0;
        for (CallbackButton button : values) {
            int buttonLen = button.getText().length();
            if ((lineLen != 0 && buttonLen + lineLen > ROW_LENGTH_LIMIT) || button.getText().equals("Back")) {
                keyboard.add(row);
                row = new ArrayList<>();
                lineLen = 0;
            }
            lineLen += buttonLen;
            row.add(new Button(button.getText(), button.getCallbackData()));
        }
        keyboard.add(row);
        return Keyboard.builder()
                .inlineKeyboard(keyboard)
                .build();
    }
}
