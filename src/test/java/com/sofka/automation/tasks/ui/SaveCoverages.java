package com.sofka.automation.tasks.ui;

import com.sofka.automation.targets.CoveragesTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;

public class SaveCoverages implements Task {

    public static SaveCoverages now() {
        return new SaveCoverages();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Click.on(CoveragesTargets.SAVE_COVERAGES_BUTTON),
            WaitUntil.the(CoveragesTargets.SAVE_COVERAGES_BUTTON, WebElementStateMatchers.isVisible())
        );
    }
}
