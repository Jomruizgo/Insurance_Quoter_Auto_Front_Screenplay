package com.sofka.automation.stepdefinitions;

import com.sofka.automation.questions.BlockingAlertsBanner;
import com.sofka.automation.questions.LocationBadgeStatus;
import com.sofka.automation.targets.LocationsTargets;
import com.sofka.automation.utils.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.screenplay.GivenWhenThen;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class LocationRegistrationStepDefinitions {

    @Before(order = 10)
    public void setStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @After
    public void drawCurtain() {
        OnStage.drawTheCurtain();
    }

    @Dado("que el agente navega a la pantalla de ubicaciones del folio")
    public void agentNavigatesToLocationsPage() {
        String folioNumber = OnStage.theActorCalled("agent").recall("folioNumber");
        String url = Constants.BASE_URL
            + String.format(Constants.LOCATIONS_URL_TEMPLATE, folioNumber);
        OnStage.theActorCalled("agent").attemptsTo(Open.url(url));
    }

    @Cuando("el agente visualiza la segunda ubicación sin datos")
    public void agentViewsSecondIncompleteLocation() {
        // no-op: page loads with all locations visible automatically
    }

    @Entonces("la segunda ubicación muestra el badge {string}")
    public void secondLocationShowsBadge(String expectedBadge) {
        OnStage.theActorCalled("agent")
            .should(seeThat(LocationBadgeStatus.forLocationIndex(2), equalTo(expectedBadge)));
    }

    @Y("aparece un banner de alertas bloqueantes en la pantalla")
    public void blockingAlertsBannerIsVisible() {
        OnStage.theActorCalled("agent")
            .should(seeThat(BlockingAlertsBanner.isVisible(), is(true)));
    }

    @Y("el agente puede navegar al siguiente paso del wizard")
    public void agentCanNavigateToNextStep() {
        OnStage.theActorCalled("agent")
            .attemptsTo(Click.on(LocationsTargets.NEXT_BUTTON));
    }

    @Cuando("el agente visualiza la primera ubicación con datos completos")
    public void agentViewsFirstCompleteLocation() {
        // no-op: page loads with all locations visible automatically
    }

    @Entonces("la primera ubicación no muestra el badge {string}")
    public void firstLocationDoesNotShowBadge(String badge) {
        OnStage.theActorCalled("agent")
            .should(seeThat(LocationBadgeStatus.forLocationIndex(1), not(equalTo(badge))));
    }

    @Y("no aparece banner de alertas bloqueantes en la pantalla")
    public void blockingAlertsBannerIsNotVisible() {
        OnStage.theActorCalled("agent")
            .should(seeThat(BlockingAlertsBanner.isVisible(), is(false)));
    }

    @Y("la primera ubicación muestra el badge {string}")
    public void firstLocationShowsBadge(String expectedBadge) {
        OnStage.theActorCalled("agent")
            .should(seeThat(LocationBadgeStatus.forLocationIndex(1), equalTo(expectedBadge)));
    }
}
