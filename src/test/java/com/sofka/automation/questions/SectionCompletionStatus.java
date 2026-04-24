package com.sofka.automation.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SectionCompletionStatus implements Question<List<String>> {

    public static SectionCompletionStatus forBothSections() {
        return new SectionCompletionStatus();
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        List<WebElement> titles = BrowseTheWeb.as(actor).getDriver()
                .findElements(By.xpath(
                        "//div[contains(@class,'card-header')]" +
                        "[.//span[contains(@class,'badge-ok')]]" +
                        "//p[@class='card-title']"));
        return titles.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
