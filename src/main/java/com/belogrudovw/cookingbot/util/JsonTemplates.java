package com.belogrudovw.cookingbot.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum JsonTemplates {
    OPEN_AI_RECIPE_REQUEST_TEMPLATE("openai_recipe_request_template.json");

    String json;

    JsonTemplates(String templatePath) {
        this.json = FilesUtil.recoverString("templates/" + templatePath);
    }
}