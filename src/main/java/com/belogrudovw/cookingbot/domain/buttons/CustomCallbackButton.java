package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class CustomCallbackButton implements CallbackButton {
    StringToken textToken;
    String callbackData;
}