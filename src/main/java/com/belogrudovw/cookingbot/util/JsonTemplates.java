package com.belogrudovw.cookingbot.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum JsonTemplates {
    // TODO: 04/02/2024 Replace by pojo build
    OPEN_AI_RECIPE_REQUEST_TEMPLATE("openai_recipe_request_template.json"),
    OPEN_AI_TRANSLATION_REQUEST_TEMPLATE("openai_translation_request_template.json"),
    SD_IMAGE_REQUEST_TEMPLATE("sd_image_request_template.json");

    String json;

    JsonTemplates(String templatePath) {
        this.json = FilesUtil.recoverString("templates/" + templatePath);
    }
}