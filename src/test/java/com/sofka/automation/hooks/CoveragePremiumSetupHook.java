package com.sofka.automation.hooks;

import com.sofka.automation.tasks.api.SetupCoveragePremiumScenario;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;

public class CoveragePremiumSetupHook {

    @Before(order = 20, value = "@premium-calculation")
    public void setupPremiumCalculationScenario() {
        OnStage.theActorCalled("agent")
            .attemptsTo(SetupCoveragePremiumScenario.withTwoLocations());
    }

    @Before(order = 20, value = "@premium-calculation-single")
    public void setupSingleLocationScenario() {
        OnStage.theActorCalled("agent")
            .attemptsTo(SetupCoveragePremiumScenario.withSingleLocation());
    }
}
