package com.sofka.automation.tasks.ui;

import com.sofka.automation.targets.CalculationTargets;
import com.sofka.automation.utils.Constants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class ExecuteCalculation implements Task {

    public static ExecuteCalculation now() {
        return new ExecuteCalculation();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String folioNumber = actor.recall("folioNumber");
        String url = Constants.BASE_URL + String.format(Constants.CALCULATION_URL_TEMPLATE, folioNumber);
        actor.attemptsTo(
            Open.url(url),
            WaitUntil.the(CalculationTargets.EXECUTE_CALCULATION_BUTTON, WebElementStateMatchers.isVisible()),
            Click.on(CalculationTargets.EXECUTE_CALCULATION_BUTTON),
            WaitUntil.the(CalculationTargets.PREMIUM_SUMMARY, WebElementStateMatchers.isVisible())
        );
    }
}
