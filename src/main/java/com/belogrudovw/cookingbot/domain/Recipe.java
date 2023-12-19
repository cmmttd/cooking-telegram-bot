package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.properties.Property;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Recipe {
    long id;
    Property property;
    String title;
    String shortDescription;
    List<CookingStep> steps;

    public record CookingStep(int delay, String title, String description) {
    }
}