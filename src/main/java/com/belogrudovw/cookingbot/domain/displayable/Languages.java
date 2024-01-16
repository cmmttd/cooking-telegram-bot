package com.belogrudovw.cookingbot.domain.displayable;

import com.belogrudovw.cookingbot.lexic.StringToken;

import java.util.Arrays;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.*;

@Getter
public enum Languages implements Displayable {
    DE(DE_TOKEN, "Deutsch"),
    FR(FR_TOKEN, "French"),
    CH(CH_TOKEN, "Chinese"),
    IT(IT_TOKEN, "Italian"),
    SP(SP_TOKEN, "Spanish"),
    LV(LV_TOKEN, "Lithuanian"),
    RU(RU_TOKEN, "Russian"),
    RS(RS_TOKEN, "Serbian"),
    UA(UA_TOKEN, "Ukrainian"),
    JP(JP_TOKEN, "Japan"),
    EN(EN_TOKEN, "English");

    private final StringToken displayable;
    private final String langName;

    Languages(StringToken stringToken, String langName) {
        this.displayable = stringToken;
        this.langName = langName;
    }

    @JsonCreator
    public static Languages from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(string.toUpperCase(Locale.ROOT)) || value.getLangName().equalsIgnoreCase(string))
                .findFirst()
                .orElse(EN);
    }
}