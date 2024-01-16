package com.belogrudovw.cookingbot.lexic;

import com.belogrudovw.cookingbot.domain.displayable.Languages;

public record SimpleStringToken(String value) implements StringToken {

    @Override
    public String in(Languages language) {
        return value;
    }
}