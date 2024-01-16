package com.belogrudovw.cookingbot.util;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.telegram.Button;
import com.belogrudovw.cookingbot.domain.telegram.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.experimental.UtilityClass;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;
import static com.belogrudovw.cookingbot.util.StringUtil.escapeCharacters;

@UtilityClass
public final class KeyboardBuilder {
    public static final int ROW_LENGTH_LIMIT = 15;

    public static Keyboard buildDefaultKeyboard(List<CallbackButton> values, Languages lang) {
        List<List<Button>> keyboard = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        int lineLen = 0;
        for (CallbackButton button : values) {
            int buttonLen = button.getTextToken().in(lang).length();
            if ((lineLen != 0 && buttonLen + lineLen > ROW_LENGTH_LIMIT) || button.getTextToken().equals(BACK_TOKEN)) {
                keyboard.add(row);
                row = new ArrayList<>();
                lineLen = 0;
            }
            lineLen += buttonLen;
            row.add(new Button(escapeCharacters(button.getTextToken().in(lang)), button.getCallbackData()));
        }
        keyboard.add(row);
        return Keyboard.builder()
                .inlineKeyboard(keyboard)
                .build();
    }

    public static Keyboard buildEmptyKeyboard() {
        return Keyboard.builder()
                .inlineKeyboard(Collections.emptyList())
                .build();
    }
}