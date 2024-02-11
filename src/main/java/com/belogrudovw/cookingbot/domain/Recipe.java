package com.belogrudovw.cookingbot.domain;

import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.domain.displayable.Lightness;
import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;
import com.belogrudovw.cookingbot.lexic.MultilingualTokens;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Recipe {
    @Id
    UUID id;
    @NotNull
    @NotEmpty
    String title;
    @NotNull
    RecipeProperties properties;
    // TODO: 13/02/2024 #46 - Add recipe translation to all availiable languages
    @NotNull
    Languages language;
    @NotNull
    @NotEmpty
    String shortDescription;
    @NotNull
    List<String> ingredients;
    @NotNull
    List<Step> steps;
    // TODO: 17/02/2024 Replace to linked list with 'getNext' access
    List<String> imageIds = new ArrayList<>();
    // FIXME: 13/02/2024 Remove when #46 complete
    String engText;

    public String toFormattedString(Languages language) {
        String minutesString = MultilingualTokens.MINUTES_TOKEN.in(language);
        String stepsString = steps.stream()
                .map(step -> "(+%d %s) %s".formatted(step.offset(), minutesString, step.title()))
                .collect(Collectors.joining("\n • ", " • ", ""));
        String ingredientsString = ingredients.stream()
                .collect(Collectors.joining("\n • ", " • ", ""));
        return """
                *%s - %s %s*
                %s
                ---
                %s
                ---
                %s
                """
                .formatted(title, properties.cookingTime, minutesString, shortDescription, ingredientsString, stepsString);
    }

    public record Step(@Positive int index,
                       @Positive int offset,
                       @NotNull @NotEmpty String title,
                       @NotNull @NotEmpty String description) {
        @Override
        public String toString() {
            return "*%d. %s*%n%s".formatted(index, title, description);
        }
    }

    public record RecipeProperties(@NotNull Lightness lightness, @NotNull MeasurementUnits units, @Positive int cookingTime) {
    }
}