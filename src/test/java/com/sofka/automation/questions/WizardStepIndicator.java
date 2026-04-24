package com.sofka.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;

public class WizardStepIndicator implements Question<String> {

    public static WizardStepIndicator currentStep() {
        return new WizardStepIndicator();
    }

    @Override
    public String answeredBy(Actor actor) {
        return BrowseTheWeb.as(actor).getDriver()
                .findElement(By.cssSelector(".step[aria-current='true'] span"))
                .getText();
    }
}
