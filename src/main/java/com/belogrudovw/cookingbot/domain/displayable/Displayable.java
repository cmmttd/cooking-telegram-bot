package com.belogrudovw.cookingbot.domain.displayable;

public interface Displayable {
    String getText();

    default String getIcon(){
        return "";
    }
}
