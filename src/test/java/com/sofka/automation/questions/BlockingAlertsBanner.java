package com.sofka.automation.questions;

import com.sofka.automation.targets.LocationsTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.List;

@Subject("whether the blocking alerts banner is visible")
public class BlockingAlertsBanner implements Question<Boolean> {

    private BlockingAlertsBanner() {}

    public static BlockingAlertsBanner isVisible() {
        return new BlockingAlertsBanner();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            List<WebElementFacade> elements =
                LocationsTargets.BLOCKING_ALERTS_BANNER.resolveAllFor(actor);
            return !elements.isEmpty() && elements.get(0).isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}
