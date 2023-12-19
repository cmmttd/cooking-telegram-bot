package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.properties.Displayable;
import com.belogrudovw.cookingbot.domain.enums.Navigational;
import com.belogrudovw.cookingbot.domain.properties.MeasurementUnits;

import lombok.Getter;

@Getter
public enum MeasurementUnitButtons implements CallbackButton {
    SETUP_UNITS_METRIC(MeasurementUnits.METRIC),
    SETUP_UNITS_IMPERIAL(MeasurementUnits.IMPERIAL),
    SETUP_UNITS_BACK(Navigational.BACK);

    private final String text;

    MeasurementUnitButtons(Displayable displayable) {
        this.text = displayable.getText();
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}