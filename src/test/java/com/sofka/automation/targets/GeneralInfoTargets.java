package com.sofka.automation.targets;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class GeneralInfoTargets {

    public static final Target PAGE_FORM = Target
            .the("formulario de datos generales")
            .located(By.cssSelector("form.page-form"));

    public static final Target RAZON_SOCIAL_INPUT = Target
            .the("campo Razón social")
            .located(By.xpath("//div[contains(@class,'field')][.//label[contains(.,'Razón social')]]//input[@class='input']"));

    public static final Target RFC_INPUT = Target
            .the("campo RFC")
            .located(By.xpath("//div[contains(@class,'field')][.//label[contains(.,'RFC')]]//input[@class='input']"));

    public static final Target EMAIL_INPUT = Target
            .the("campo Correo de contacto")
            .located(By.xpath("//div[contains(@class,'field')][.//label[contains(.,'Correo de contacto')]]//input[@class='input']"));

    public static final Target PHONE_INPUT = Target
            .the("campo Teléfono")
            .located(By.xpath("//div[contains(@class,'field')][.//label[contains(.,'Teléfono')]]//input[@class='input']"));

    public static final Target RISK_CLASSIFICATION_DROPDOWN = Target
            .the("dropdown Clasificación de riesgo")
            .located(By.xpath("//div[contains(@class,'field')][.//label[contains(.,'Clasificación de riesgo')]]//select[@class='select']"));

    public static final Target BUSINESS_TYPE_DROPDOWN = Target
            .the("dropdown Tipo de negocio")
            .located(By.xpath("//div[contains(@class,'field')][.//label[contains(.,'Tipo de negocio')]]//select[@class='select']"));

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
