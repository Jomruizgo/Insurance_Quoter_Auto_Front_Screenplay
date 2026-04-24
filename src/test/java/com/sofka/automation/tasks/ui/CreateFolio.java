package com.sofka.automation.tasks.ui;

import com.sofka.automation.targets.DashboardTargets;
import com.sofka.automation.targets.GeneralInfoTargets;
import com.sofka.automation.utils.Constants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateFolio implements Task {

    public static CreateFolio fromDashboard() {
        return Tasks.instrumented(CreateFolio.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(Constants.BASE_URL),
                Click.on(DashboardTargets.NEW_FOLIO_BUTTON),
                WaitUntil.the(DashboardTargets.SUBSCRIBER_DROPDOWN, WebElementStateMatchers.isVisible()),
                SelectFromOptions.byIndex(1).from(DashboardTargets.SUBSCRIBER_DROPDOWN),
                SelectFromOptions.byIndex(1).from(DashboardTargets.AGENT_DROPDOWN),
                Click.on(DashboardTargets.CREATE_FOLIO_BUTTON),
                WaitUntil.the(GeneralInfoTargets.PAGE_FORM, WebElementStateMatchers.isVisible())
        );

        String currentUrl = BrowseTheWeb.as(actor).getDriver().getCurrentUrl();
        Pattern pattern = Pattern.compile("(FOL-\\d{4}-\\d+)");
        Matcher matcher = pattern.matcher(currentUrl);
        if (matcher.find()) {
            actor.remember("folioNumber", matcher.group(1));
        }
    }
}
