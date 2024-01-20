package com.belogrudovw.cookingbot.lexic;

import com.belogrudovw.cookingbot.domain.displayable.Languages;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JoinedStringToken implements StringToken {

    List<StringToken> appended;

    public JoinedStringToken(StringToken... tokens) {
        this.appended = Collections.unmodifiableList(Arrays.asList(tokens));
    }

    @Override
    public String in(Languages language) {
        return appended.stream()
                .map(x -> x.in(language))
                .collect(Collectors.joining());
    }
}