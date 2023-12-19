package com.belogrudovw.cookingbot.domain.properties;

public interface Displayable {
    String getText();

    default String getIcon(){
        return "";
    }
}
