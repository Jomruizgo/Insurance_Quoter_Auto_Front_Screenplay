package com.sofka.automation.tasks.ui;

import com.sofka.automation.targets.GeneralInfoTargets;
import com.sofka.automation.utils.Constants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.SelectFromOptions;

public class CompleteGeneralInfo implements Task {

    public static CompleteGeneralInfo withDefaultTestData() {
        return Tasks.instrumented(CompleteGeneralInfo.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(Constants.TEST_RAZON_SOCIAL).into(GeneralInfoTargets.RAZON_SOCIAL_INPUT),
                Enter.theValue(Constants.TEST_RFC).into(GeneralInfoTargets.RFC_INPUT),
                Enter.theValue(Constants.TEST_EMAIL).into(GeneralInfoTargets.EMAIL_INPUT),
                Enter.theValue(Constants.TEST_PHONE).into(GeneralInfoTargets.PHONE_INPUT),
                SelectFromOptions.byIndex(1).from(GeneralInfoTargets.RISK_CLASSIFICATION_DROPDOWN),
                SelectFromOptions.byIndex(1).from(GeneralInfoTargets.BUSINESS_TYPE_DROPDOWN)
        );
    }
}
