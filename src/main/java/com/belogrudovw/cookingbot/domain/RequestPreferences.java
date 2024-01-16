package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.displayable.Difficulties;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class RequestPreferences {
    Languages language;
    Lightness lightness;
    MeasurementUnits units;
    Difficulties difficulty;

    public boolean matchesTo(@NonNull Recipe.RecipeProperties target) {
        return (this.lightness == Lightness.ANY || this.lightness == target.lightness())
                && this.units == target.units()
                && this.difficulty.getMinutes() >= target.cookingTime();
    }

    public boolean isEmpty() {
        return language == null || lightness == null || units == null || difficulty == null;
    }
}