package com.belogrudovw.cookingbot.domain.buttons;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class CustomCallbackButton implements CallbackButton {
    String text;
    String callbackData;
}