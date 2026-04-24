package com.sofka.automation.tasks.api;

import com.sofka.automation.utils.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.Map;

public class SetupLocationScenario implements Task {

    public static SetupLocationScenario forCurrentActor() {
        return new SetupLocationScenario();
    }

    @Override
    @Step("{0} sets up a folio with one complete and one incomplete location via API")
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = RestAssured.baseURI.isBlank()
            ? Constants.BACKEND_BASE_URL
            : RestAssured.baseURI;

        // 1. POST /v1/folios
        Response createFolioResponse = RestAssured
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

        if (createFolioResponse.statusCode() < 200 || createFolioResponse.statusCode() >= 300) {
            throw new IllegalStateException(
                "Failed to create folio. Status: " + createFolioResponse.statusCode()
                + " Body: " + createFolioResponse.body().asString());
        }

        long folioId = createFolioResponse.jsonPath().getLong("id");
        String folioNumber = createFolioResponse.jsonPath().getString("folioNumber");

        // 2. PUT /v1/folios/{id}/general-info
        Response generalInfoResponse = RestAssured
            .given()
                .baseUri(Constants.BACKEND_BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "razonSocial", Constants.TEST_RAZON_SOCIAL,
                    "rfc", Constants.TEST_RFC,
                    "email", Constants.TEST_EMAIL,
                    "phone", Constants.TEST_PHONE,
                    "riskClassification", "BAJO",
                    "businessType", "COMERCIAL",
                    "version", 0
                ))
            .when()
                .put("/v1/folios/" + folioId + "/general-info")
            .then()
                .extract().response();

        if (generalInfoResponse.statusCode() < 200 || generalInfoResponse.statusCode() >= 300) {
            throw new IllegalStateException(
                "Failed to set general info for folio " + folioId
                + ". Status: " + generalInfoResponse.statusCode()
                + " Body: " + generalInfoResponse.body().asString());
        }

        // 3. PUT /v1/quotes/{folioNumber}/locations — 2 ubicaciones
        Map<String, Object> completeLocation = Map.of(
            "index", 1,
            "locationName", "Ubicación 1",
            "address", "Reforma 222",
            "zipCode", Constants.TEST_LOCATION_ZIP_CODE,
            "constructionType", "MASONRY",
            "level", 1,
            "constructionYear", 2010,
            "businessLine", Map.of(
                "code", Constants.TEST_LOCATION_BL_CODE,
                "fireKey", Constants.TEST_LOCATION_BL_FIRE_KEY
            ),
            "guarantees", List.of(Map.of(
                "code", Constants.TEST_LOCATION_GUARANTEE_CODE,
                "insuredValue", Constants.TEST_LOCATION_INSURED_VALUE
            ))
        );

        Map<String, Object> incompleteLocation = Map.of(
            "index", 2,
            "locationName", "Ubicación 2",
            "address", "",
            "zipCode", "",
            "constructionType", "MASONRY",
            "level", 1,
            "constructionYear", 2000,
            "guarantees", List.of()
        );

        Response locationsResponse = RestAssured
            .given()
                .baseUri(Constants.BACKEND_BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "locations", List.of(completeLocation, incompleteLocation),
                    "version", 0
                ))
            .when()
                .put("/v1/quotes/" + folioNumber + "/locations")
            .then()
                .extract().response();

        if (locationsResponse.statusCode() < 200 || locationsResponse.statusCode() >= 300) {
            throw new IllegalStateException(
                "Failed to set locations for folio " + folioNumber
                + ". Status: " + locationsResponse.statusCode()
                + " Body: " + locationsResponse.body().asString());
        }

        actor.remember("folioNumber", folioNumber);
        actor.remember("folioId", String.valueOf(folioId));
    }
}
