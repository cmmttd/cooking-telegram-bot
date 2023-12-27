package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.displayable.Cuisines;
import com.belogrudovw.cookingbot.domain.displayable.Difficulties;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Property {
    Languages language;
    Cuisines cuisine;
    Lightness lightness;
    MeasurementUnits units;
    Difficulties difficulty;

    public boolean matchesTo(Property target) {
        return this.language == target.language
                && (this.lightness == target.lightness || target.lightness == Lightness.ANY)
                && this.units == target.units
                && (this.difficulty == target.difficulty || target.difficulty == Difficulties.MINUTES_INFINITY);
    }

    public boolean isEmpty() {
        return language == null || lightness == null || units == null || difficulty == null;
    }
}