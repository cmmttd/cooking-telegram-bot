package com.belogrudovw.cookingbot.lexic;

import com.belogrudovw.cookingbot.domain.displayable.Languages;

import lombok.Getter;

@Getter
public enum SingleValueTokens implements StringToken {
    // TODO: 16/01/2024 Consider more readable way to setup
    EMPTY_TOKEN(""),
    NEXT_TOKEN("⏩"),
    CANCEL_TOKEN("⏹️"),
    PAUSE_TOKEN("⏸️"),
    BACK_TOKEN("↩️"),
    SPIN_TOKEN("🔄"),
    START_TOKEN("▶️"),
    DE_TOKEN("   🇩🇪  "),
    FR_TOKEN("   🇫🇷  "),
    CH_TOKEN("   🇨🇳  "),
    IT_TOKEN("   🇮🇹  "),
    SP_TOKEN("   🇪🇸  "),
    LV_TOKEN("   🇱🇹  "),
    RU_TOKEN("   🇷🇺   "),
    RS_TOKEN("   🇷🇸  "),
    UA_TOKEN("   🇺🇦  "),
    JP_TOKEN("   🇯🇵  "),
    EN_TOKEN("  🇬🇧/🇺🇸 "),
    SUCCESS_BUTTON_TOKEN("💕"),
    MINUTES_15_TOKEN("15 min"),
    MINUTES_30_TOKEN("30 min"),
    MINUTES_60_TOKEN("60 min");

    private final String token;

    SingleValueTokens(String value) {
        this.token = value;
    }

    @Override
    public String in(Languages lang) {
        return token;
    }
}