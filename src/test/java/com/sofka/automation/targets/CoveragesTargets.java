package com.sofka.automation.targets;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class CoveragesTargets {

    public static final Target COVERAGES_GRID = Target.the("coverage options grid")
        .located(By.cssSelector("app-coverage-options-grid"));

    public static final Target LOCATION_TAB_SELECTOR = Target.the("location tab selector")
        .located(By.cssSelector("app-location-tab-selector"));

    public static final Target SAVE_COVERAGES_BUTTON = Target.the("guardar coberturas button")
        .locatedBy("//div[contains(@class,'technical-info-page__nav')]"
            + "//button[normalize-space(.)='Guardar coberturas']");

    public static Target coverageToggleByCode(String code) {
        return Target.the("toggle for coverage " + code)
            .locatedBy("//div[contains(@class,'coverage-card')]"
                + "[.//code[contains(@class,'coverage-card__code') and normalize-space()='" + code + "']]"
                + "//app-switch");
    }

    private CoveragesTargets() {}
}
