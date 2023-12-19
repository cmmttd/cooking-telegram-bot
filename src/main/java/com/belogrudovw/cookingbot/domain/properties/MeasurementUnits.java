package com.belogrudovw.cookingbot.domain.properties;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum MeasurementUnits implements Displayable {
    METRIC("Metric (kg, gram, liter, etc)"),
    IMPERIAL("Imperial (inch, feet, gallon, etc)");

    private final String text;

    MeasurementUnits(String string) {
        this.text = string;
    }


    public static MeasurementUnits from(String string) {
        return Arrays.stream(values())
                .filter(value -> value.getText().equals(string))
                .findFirst()
                .orElse(METRIC);
    }
}