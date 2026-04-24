package com.sofka.automation.questions;

import com.sofka.automation.targets.LocationsTargets;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.List;

@Subject("the badge text for location #?index")
public class LocationBadgeStatus implements Question<String> {

    private final int index;

    private LocationBadgeStatus(int index) {
        this.index = index;
    }

    public static LocationBadgeStatus forLocationIndex(int index) {
        return new LocationBadgeStatus(index);
    }

    @Override
    public String answeredBy(Actor actor) {
        try {
            Target badge = LocationsTargets.badgeForIndex(index);
            List<WebElementFacade> elements = badge.resolveAllFor(actor);
            if (elements.isEmpty()) {
                return "";
            }
            return elements.get(0).getText().trim().replaceAll("\\s*\\(\\d+\\)$", "");
        } catch (Exception e) {
            return "";
        }
    }
}
