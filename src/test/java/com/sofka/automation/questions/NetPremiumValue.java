package com.sofka.automation.questions;

import com.sofka.automation.targets.CalculationTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;

@Subject("the net premium value")
public class NetPremiumValue implements Question<String> {

    public static NetPremiumValue displayed() {
        return new NetPremiumValue();
    }

    @Override
    public String answeredBy(Actor actor) {
        try {
            String raw = CalculationTargets.NET_PREMIUM_VALUE.resolveFor(actor).getText();
            return raw.replaceAll("[^0-9.]", "").trim();
        } catch (Exception e) {
            return "0";
        }
    }
}
