package com.belogrudovw.cookingbot.domain.buttons;

import com.belogrudovw.cookingbot.domain.displayable.MeasurementUnits;
import com.belogrudovw.cookingbot.lexic.StringToken;

import lombok.Getter;

import static com.belogrudovw.cookingbot.lexic.SingleValueTokens.BACK_TOKEN;

@Getter
public enum MeasurementUnitButtons implements CallbackButton {
    SETUP_UNITS_METRIC(MeasurementUnits.METRIC),
    SETUP_UNITS_IMPERIAL(MeasurementUnits.IMPERIAL),
    SETUP_UNITS_BACK(BACK_TOKEN);

    private final StringToken textToken;
    private final MeasurementUnits measurementUnits;

    MeasurementUnitButtons(MeasurementUnits measurementUnits) {
        this.measurementUnits = measurementUnits;
        this.textToken = measurementUnits.getDisplayable();
    }

    MeasurementUnitButtons(StringToken stringToken) {
        this.measurementUnits = null;
        this.textToken = stringToken;
    }

    @Override
    public String getCallbackData() {
        return this.name();
    }
}