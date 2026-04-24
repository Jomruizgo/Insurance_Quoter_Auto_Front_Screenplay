package com.sofka.automation.stepdefinitions;

import com.sofka.automation.questions.CommercialPremiumValue;
import com.sofka.automation.questions.LocationCalculationStatus;
import com.sofka.automation.questions.NetPremiumValue;
import com.sofka.automation.targets.CalculationTargets;
import com.sofka.automation.targets.CoveragesTargets;
import com.sofka.automation.tasks.ui.ActivateCoverageForLocation;
import com.sofka.automation.tasks.ui.ExecuteCalculation;
import com.sofka.automation.tasks.ui.SaveCoverages;
import com.sofka.automation.utils.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.List;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

public class PremiumCalculationStepDefinitions {

    @Before(order = 10)
    public void setStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @After
    public void drawCurtain() {
        OnStage.drawTheCurtain();
    }

    @Dado("que el agente navega a la pantalla de coberturas del folio")
    public void agentNavigatesToCoveragesPage() {
        String folioNumber = OnStage.theActorCalled("agent").recall("folioNumber");
        String url = Constants.BASE_URL + String.format(Constants.TECHNICAL_INFO_URL_TEMPLATE, folioNumber);
        OnStage.theActorCalled("agent").attemptsTo(
            Open.url(url),
            WaitUntil.the(CoveragesTargets.COVERAGES_GRID, WebElementStateMatchers.isVisible())
        );
    }

    @Cuando("el agente activa la cobertura de incendio en la ubicación completa y guarda")
    public void agentActivatesFireCoverageAndSaves() {
        OnStage.theActorCalled("agent").attemptsTo(
            ActivateCoverageForLocation.withCode(Constants.COVERAGE_CODE_FIRE),
            SaveCoverages.now()
        );
    }

    @Y("el agente ejecuta el cálculo de prima")
    public void agentExecutesPremiumCalculation() {
        OnStage.theActorCalled("agent").attemptsTo(
            ExecuteCalculation.now()
        );
    }

    @Entonces("la prima neta es mayor a cero")
    public void netPremiumIsGreaterThanZero() {
        String raw = NetPremiumValue.displayed().answeredBy(OnStage.theActorCalled("agent"));
        double value = raw.isEmpty() ? 0.0 : Double.parseDouble(raw);
        if (value <= 0) {
            throw new AssertionError("La prima neta debe ser mayor a cero. Valor actual: " + raw);
        }
    }

    @Y("la prima comercial es mayor a cero")
    public void commercialPremiumIsGreaterThanZero() {
        String raw = CommercialPremiumValue.displayed().answeredBy(OnStage.theActorCalled("agent"));
        double value = raw.isEmpty() ? 0.0 : Double.parseDouble(raw);
        if (value <= 0) {
            throw new AssertionError("La prima comercial debe ser mayor a cero. Valor actual: " + raw);
        }
    }

    @Entonces("la primera ubicación no aparece como {string}")
    public void firstLocationDoesNotAppearAs(String status) {
        List<WebElementFacade> elements =
            CalculationTargets.noCalculableBadgeForLocation(1)
                .resolveAllFor(OnStage.theActorCalled("agent"));
        if (!elements.isEmpty()) {
            throw new AssertionError(
                "La primera ubicación no debería mostrar '" + status + "' pero el badge está visible");
        }
    }

    @Entonces("la ubicación incompleta aparece como {string}")
    public void incompleteLocationAppearsAs(String expectedStatus) {
        OnStage.theActorCalled("agent")
            .should(seeThat(LocationCalculationStatus.ofIncompleteLocation(), equalTo(expectedStatus)));
    }
}
