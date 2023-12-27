package com.belogrudovw.cookingbot.domain;

import java.util.List;
import java.util.UUID;

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
public final class Recipe {
    UUID id;
    Property property;
    String title;
    String shortDescription;
    int cookingTime;
    List<CookingStep> steps;

    @Override
    public String toString() {
        return "*%s - %d min*%n%s".formatted(title, cookingTime, shortDescription);
    }

    public record CookingStep(int offset, String title, String description) {
        @Override
        public String toString() {
            return "*%s*%n%s".formatted(title, description);
        }
    }
}