package com.sofka.automation.targets;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class GeneralInfoTargets {

    public static final Target PAGE_FORM = Target
            .the("formulario de datos generales")
            .located(By.cssSelector("form.page-form"));

    public static final Target RAZON_SOCIAL_INPUT = Target
            .the("campo Razón social")
            .located(By.cssSelector("app-input[formcontrolname='name'] input.input"));

    public static final Target RFC_INPUT = Target
            .the("campo RFC")
            .located(By.cssSelector("app-input[formcontrolname='rfc'] input.input"));

    public static final Target EMAIL_INPUT = Target
            .the("campo Correo de contacto")
            .located(By.cssSelector("app-input[formcontrolname='email'] input.input"));

    public static final Target PHONE_INPUT = Target
            .the("campo Teléfono")
            .located(By.cssSelector("app-input[formcontrolname='phone'] input.input"));

    public static final Target RISK_CLASSIFICATION_DROPDOWN = Target
            .the("dropdown Clasificación de riesgo")
            .located(By.cssSelector("app-select[formcontrolname='riskClassification'] select.select"));

    public static final Target BUSINESS_TYPE_DROPDOWN = Target
            .the("dropdown Tipo de negocio")
            .located(By.cssSelector("app-select[formcontrolname='businessType'] select.select"));

    public static final Target NEXT_BUTTON = Target
            .the("botón Siguiente")
            .located(By.cssSelector("button[type='submit'].btn--primary"));

    public static final Target SECTION_COMPLETE_BADGE = Target
            .the("badge sección completa")
            .located(By.cssSelector(".card-header span.badge-ok"));

    public static final Target WIZARD_ACTIVE_STEP = Target
            .the("paso activo del wizard")
            .located(By.cssSelector(".step[aria-current='true'] span"));
}
