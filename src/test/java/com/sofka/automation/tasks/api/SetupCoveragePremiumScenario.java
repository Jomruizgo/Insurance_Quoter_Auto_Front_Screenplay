package com.sofka.automation.tasks.api;

import com.sofka.automation.utils.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupCoveragePremiumScenario implements Task {

    private final boolean twoLocations;

    private SetupCoveragePremiumScenario(boolean twoLocations) {
        this.twoLocations = twoLocations;
    }

    public static SetupCoveragePremiumScenario withTwoLocations() {
        return new SetupCoveragePremiumScenario(true);
    }

    public static SetupCoveragePremiumScenario withSingleLocation() {
        return new SetupCoveragePremiumScenario(false);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Response folioResponse = createFolio();
        String folioNumber = folioResponse.jsonPath().getString("folioNumber");
        long version = folioResponse.jsonPath().getLong("version");

        Response generalInfoResponse = updateGeneralInfo(folioNumber, version);
        version = generalInfoResponse.jsonPath().getLong("version");

        setLocations(folioNumber, version);
        actor.remember("folioNumber", folioNumber);
    }

    private Response createFolio() {
        Response response = RestAssured
            .given()
                .baseUri(Constants.BACKEND_BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "subscriberId", Constants.TEST_SUBSCRIBER_ID,
                    "agentCode", Constants.TEST_AGENT_CODE
                ))
            .when()
                .post("/v1/folios")
            .then()
                .extract().response();

        assertSuccess(response, "POST /v1/folios");
        return response;
    }

    private Response updateGeneralInfo(String folioNumber, long version) {
        Map<String, Object> insuredData = Map.of(
            "name", Constants.TEST_RAZON_SOCIAL,
            "rfc", Constants.TEST_RFC,
            "email", Constants.TEST_EMAIL,
            "phone", Constants.TEST_PHONE
        );
        Map<String, Object> underwritingData = Map.of(
            "subscriberId", Constants.TEST_SUBSCRIBER_ID,
            "agentCode", Constants.TEST_AGENT_CODE,
            "riskClassification", Constants.TEST_RISK_CLASSIFICATION,
            "businessType", Constants.TEST_BUSINESS_TYPE
        );

        Response response = RestAssured
            .given()
                .baseUri(Constants.BACKEND_BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "insuredData", insuredData,
                    "underwritingData", underwritingData,
                    "version", version
                ))
            .when()
                .put("/v1/quotes/" + folioNumber + "/general-info")
            .then()
                .extract().response();

        assertSuccess(response, "PUT /v1/quotes/" + folioNumber + "/general-info");
        return response;
    }

    private void setLocations(String folioNumber, long version) {
        Map<String, Object> completeLocation = Map.of(
            "index", 1,
            "locationName", "Ubicación 1",
            "address", "Reforma 222",
            "zipCode", Constants.TEST_LOCATION_ZIP_CODE,
            "constructionType", "MASONRY",
            "level", 1,
            "constructionYear", 2010,
            "businessLine", Map.of(
                "code", Constants.COVERAGE_TEST_BL_CODE,
                "fireKey", Constants.COVERAGE_TEST_BL_FIRE_KEY
            ),
            "guarantees", List.of(Map.of(
                "code", Constants.COVERAGE_TEST_GUARANTEE_CODE,
                "insuredValue", Constants.TEST_LOCATION_INSURED_VALUE
            ))
        );

        List<Map<String, Object>> locations;
        if (twoLocations) {
            Map<String, Object> incompleteLocation = new HashMap<>();
            incompleteLocation.put("index", 2);
            incompleteLocation.put("locationName", "Ubicación 2");
            incompleteLocation.put("address", "");
            incompleteLocation.put("zipCode", "");
            incompleteLocation.put("constructionType", "MASONRY");
            incompleteLocation.put("level", 1);
            incompleteLocation.put("constructionYear", 2000);
            incompleteLocation.put("businessLine", null);
            incompleteLocation.put("guarantees", List.of());
            locations = List.of(completeLocation, incompleteLocation);
        } else {
            locations = List.of(completeLocation);
        }

        Response response = RestAssured
            .given()
                .baseUri(Constants.BACKEND_BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "locations", locations,
                    "version", version
                ))
            .when()
                .put("/v1/quotes/" + folioNumber + "/locations")
            .then()
                .extract().response();

        assertSuccess(response, "PUT /v1/quotes/" + folioNumber + "/locations");
    }

    private void assertSuccess(Response response, String operation) {
        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new IllegalStateException(
                operation + " returned " + status + ": " + response.body().asString());
        }
    }
}
