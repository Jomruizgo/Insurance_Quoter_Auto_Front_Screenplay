package com.sofka.automation.targets;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class CalculationTargets {

    public static final Target EXECUTE_CALCULATION_BUTTON = Target.the("ejecutar cálculo button")
        .locatedBy("//button[normalize-space(.)='Ejecutar cálculo']");

    public static final Target PREMIUM_SUMMARY = Target.the("premium summary")
        .located(By.cssSelector(".premium-summary"));

    public static final Target NET_PREMIUM_VALUE = Target.the("net premium value")
        .located(By.cssSelector(".premium-summary__card--dark .premium-summary__card-value"));

    public static final Target COMMERCIAL_PREMIUM_VALUE = Target.the("commercial premium value")
        .located(By.cssSelector(".premium-summary__card--brand .premium-summary__card-value"));

    public static final Target NO_CALCULABLE_BADGE = Target.the("no calculable badge")
        .locatedBy("//li[contains(@class,'premium-summary__location-item')]"
            + "//app-badge[normalize-space(.)='No calculable']");

    public static Target noCalculableBadgeForLocation(int locationIndex) {
        return Target.the("no calculable badge for location " + locationIndex)
            .locatedBy("(//li[contains(@class,'premium-summary__location-item')])[" + locationIndex + "]"
                + "//app-badge[normalize-space(.)='No calculable']");
    }

    private CalculationTargets() {}
}
