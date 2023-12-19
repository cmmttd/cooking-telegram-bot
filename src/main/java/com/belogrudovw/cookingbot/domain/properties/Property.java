package com.belogrudovw.cookingbot.domain.properties;

import lombok.*;
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
}