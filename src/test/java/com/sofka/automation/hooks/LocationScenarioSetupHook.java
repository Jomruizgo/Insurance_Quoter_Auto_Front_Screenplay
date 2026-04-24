package com.sofka.automation.hooks;

import com.sofka.automation.tasks.api.SetupLocationScenario;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;

public class LocationScenarioSetupHook {

    @Before(order = 20, value = "@location-registration")
    public void setupLocationScenario() {
        OnStage.theActorCalled("agent")
            .attemptsTo(SetupLocationScenario.forCurrentActor());
    }
}
