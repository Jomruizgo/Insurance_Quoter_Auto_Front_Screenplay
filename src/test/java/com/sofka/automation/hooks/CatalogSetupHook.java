package com.sofka.automation.hooks;

import com.sofka.automation.utils.Constants;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.List;

public class CatalogSetupHook {

    @Before(order = 1)
    public void ensureCatalogsExist() {
        verifySubscriberExists();
        verifyAgentExists();
    }

    private void verifySubscriberExists() {
        Response response = RestAssured
                .given().baseUri(Constants.CORE_BASE_URL)
                .get("/v1/subscribers");

        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new IllegalStateException(
                    "CatalogSetupHook: GET /v1/subscribers retornó " + response.getStatusCode());
        }

        List<String> ids = response.jsonPath().getList("subscribers.id", String.class);
        if (ids == null || !ids.contains(Constants.TEST_SUBSCRIBER_ID)) {
            throw new IllegalStateException(
                    "CatalogSetupHook: suscriptor '" + Constants.TEST_SUBSCRIBER_ID
                    + "' no encontrado en el catálogo. IDs disponibles: " + ids);
        }
    }

    private void verifyAgentExists() {
        Response response = RestAssured
                .given().baseUri(Constants.CORE_BASE_URL)
                .get("/v1/agents");

        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new IllegalStateException(
                    "CatalogSetupHook: GET /v1/agents retornó " + response.getStatusCode());
        }

        List<String> codes = response.jsonPath().getList("agents.code", String.class);
        if (codes == null || !codes.contains(Constants.TEST_AGENT_CODE)) {
            throw new IllegalStateException(
                    "CatalogSetupHook: agente '" + Constants.TEST_AGENT_CODE
                    + "' no encontrado en el catálogo. Códigos disponibles: " + codes);
        }
    }
}
