package com.sofka.automation.questions;

import com.sofka.automation.targets.CalculationTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.List;

@Subject("the calculation status of the location")
public class LocationCalculationStatus implements Question<String> {

    public static LocationCalculationStatus ofIncompleteLocation() {
        return new LocationCalculationStatus();
    }

    @Override
    public String answeredBy(Actor actor) {
        try {
            List<WebElementFacade> elements =
                CalculationTargets.NO_CALCULABLE_BADGE.resolveAllFor(actor);
            return elements.isEmpty() ? "Calculable" : "No calculable";
        } catch (Exception e) {
            return "Calculable";
        }
    }
}
