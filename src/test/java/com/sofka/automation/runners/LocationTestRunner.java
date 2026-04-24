package com.sofka.automation.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/location_registration.feature")
@ConfigurationParameters({
        @ConfigurationParameter(
                key = "cucumber.glue",
                value = "com.sofka.automation.stepdefinitions,com.sofka.automation.hooks"),
        @ConfigurationParameter(
                key = "cucumber.plugin",
                value = "io.cucumber.core.plugin.SerenityReporterParallel")
})
public class LocationTestRunner {
}
