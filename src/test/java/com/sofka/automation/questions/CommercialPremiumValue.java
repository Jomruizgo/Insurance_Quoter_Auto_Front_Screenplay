package com.sofka.automation.questions;

import com.sofka.automation.targets.CalculationTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

@Subject("the commercial premium value")
public class CommercialPremiumValue implements Question<String> {

    public static CommercialPremiumValue displayed() {
        return new CommercialPremiumValue();
    }

    @Override
    public String answeredBy(Actor actor) {
        try {
            String raw = CalculationTargets.COMMERCIAL_PREMIUM_VALUE.resolveFor(actor).getText();
            return raw.replaceAll("[^0-9.]", "").trim();
        } catch (Exception e) {
            return "0";
        }
    }
}
