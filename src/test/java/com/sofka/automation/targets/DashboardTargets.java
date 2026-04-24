package com.sofka.automation.targets;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class DashboardTargets {

    public static final Target NEW_FOLIO_BUTTON = Target
            .the("botón Nuevo folio")
            .located(By.xpath("//button[contains(normalize-space(.), 'Nuevo folio')]"));

    public static final Target MODAL = Target
            .the("modal de creación de folio")
            .located(By.cssSelector(".modal[role='dialog']"));

    public static final Target SUBSCRIBER_DROPDOWN = Target
            .the("dropdown Suscriptor en modal")
            .located(By.cssSelector("#subscriberId select.select"));

    public static final Target AGENT_DROPDOWN = Target
            .the("dropdown Agente en modal")
            .located(By.cssSelector("#agentCode select.select"));

    public static final Target CREATE_FOLIO_BUTTON = Target
            .the("botón Crear folio en modal")
            .located(By.xpath("//div[contains(@class,'modal__footer')]//button[contains(normalize-space(.),'Crear folio')]"));
}
