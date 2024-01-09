package com.belogrudovw.cookingbot.domain.displayable;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum MeasurementUnits implements Displayable {
    METRIC("Metric (kg, gram, liter, etc)"),
    IMPERIAL("Imperial (inch, feet, gallon, etc)");

    private final String text;

    MeasurementUnits(String string) {
        this.text = string;
    }

    @JsonCreator
    public static MeasurementUnits from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(string) || value.name().equalsIgnoreCase(string))
                .findFirst()
                .orElse(METRIC);
    }
}