package com.sofka.automation.tasks.ui;

import com.sofka.automation.targets.CoveragesTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import net.serenitybdd.screenplay.targets.Target;

public class ActivateCoverageForLocation implements Task {

    private final String coverageCode;

    private ActivateCoverageForLocation(String coverageCode) {
        this.coverageCode = coverageCode;
    }

    public static ActivateCoverageForLocation withCode(String coverageCode) {
        return new ActivateCoverageForLocation(coverageCode);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Target toggle = CoveragesTargets.coverageToggleByCode(coverageCode);
        Target badgeActiva = Target.the("badge activa for " + coverageCode)
            .locatedBy("//div[contains(@class,'coverage-card')]"
                + "[.//code[contains(@class,'coverage-card__code') and normalize-space()='" + coverageCode + "']]"
                + "//app-badge[contains(normalize-space(.),'Activa')]");

        actor.attemptsTo(
            WaitUntil.the(toggle, WebElementStateMatchers.isVisible()),
            Click.on(toggle),
            WaitUntil.the(badgeActiva, WebElementStateMatchers.isVisible())
        );
    }
}
