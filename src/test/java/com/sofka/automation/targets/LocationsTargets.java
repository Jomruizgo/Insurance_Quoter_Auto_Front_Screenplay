package com.sofka.automation.targets;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class LocationsTargets {

    public static Target badgeForIndex(int index) {
        return Target.the("badge for location " + index)
            .locatedBy("//table[contains(@class,'locations-table')]"
                + "//tbody/tr[td[2][normalize-space(text())='" + index + "']]"
                + "/td[last()]//span");
    }

    public static final Target LOCATIONS_TABLE = Target.the("tabla de ubicaciones")
        .located(By.cssSelector("table.locations-table"));

    public static final Target BLOCKING_ALERTS_BANNER = Target.the("blocking alerts banner")
        .located(By.cssSelector("div.alert-banner.alert-banner--warn"));

    public static final Target NEXT_BUTTON = Target.the("siguiente button")
        .locatedBy("//div[contains(@class,'page-nav')]"
            + "//button[contains(normalize-space(.),'Siguiente')]");

    private LocationsTargets() {}
}
