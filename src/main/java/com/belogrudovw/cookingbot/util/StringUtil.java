package com.belogrudovw.cookingbot.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class StringUtil {

    private static final String REGEX = "[\\[+\\]+:{}^~?\\\\/()><=\"!.,-]";

    public static String escapeCharacters(String inp) {
        return inp.replaceAll(REGEX, "\\\\$0");
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
