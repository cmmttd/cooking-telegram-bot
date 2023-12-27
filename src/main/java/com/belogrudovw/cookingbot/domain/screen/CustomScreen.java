package com.belogrudovw.cookingbot.domain.screen;

import com.belogrudovw.cookingbot.domain.buttons.CallbackButton;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class CustomScreen implements Screen {
    String text;
    List<CallbackButton> buttons;
}