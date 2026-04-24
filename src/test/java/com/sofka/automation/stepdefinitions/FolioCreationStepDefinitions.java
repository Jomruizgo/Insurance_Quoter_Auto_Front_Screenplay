package com.sofka.automation.stepdefinitions;

import com.sofka.automation.questions.SectionCompletionStatus;
import com.sofka.automation.questions.WizardStepIndicator;
import com.sofka.automation.targets.GeneralInfoTargets;
import com.sofka.automation.tasks.ui.CompleteGeneralInfo;
import com.sofka.automation.tasks.ui.CreateFolio;
import com.sofka.automation.utils.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.cast.Cast;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.stage.OnStage;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static org.assertj.core.api.Assertions.assertThat;

public class FolioCreationStepDefinitions {

    @Before(order = 10)
    public void setTheStage() {
        OnStage.setTheStage(Cast.whereEveryoneCanBrowseTheWeb());
    }

    @After
    public void teardown() {
        OnStage.drawTheCurtain();
    }

    @Given("que el agente está en el panel de cotizaciones")
    public void elAgenteEstaEnElPanel() {
        OnStage.theActorCalled("Agente").attemptsTo(
                Open.url(Constants.BASE_URL)
        );
    }

    @When("crea un nuevo folio seleccionando suscriptor y agente")
    public void creaUnNuevoFolio() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                CreateFolio.fromDashboard()
        );
    }

    @And("completa los datos del asegurado y suscripción")
    public void completaLosDatos() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                CompleteGeneralInfo.withDefaultTestData()
        );
    }

    @Then("ambas secciones muestran estado completo y el folio avanza al paso de layout")
    public void verificarEstadoCompleto() {
        Actor actor = OnStage.theActorInTheSpotlight();

        assertThat(actor.asksAbout(SectionCompletionStatus.forBothSections()))
                .as("Las secciones Asegurado y Suscripción deben mostrar badge Completo")
                .contains("Asegurado", "Suscripción");

        actor.attemptsTo(
                Click.on(GeneralInfoTargets.NEXT_BUTTON),
                WaitUntil.the(GeneralInfoTargets.WIZARD_ACTIVE_STEP, WebElementStateMatchers.isVisible())
        );

        assertThat(actor.asksAbout(WizardStepIndicator.currentStep()))
                .as("El wizard debe estar en el paso Layout")
                .containsIgnoringCase("Layout");
    }
}
