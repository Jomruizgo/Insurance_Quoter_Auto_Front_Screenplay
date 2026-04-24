package com.sofka.automation.hooks;

import com.sofka.automation.utils.Constants;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;

public class CatalogSetupHook {

    @Before(order = 1)
    public void ensureCatalogsExist() {
        ensureSubscriberExists();
        ensureAgentExists();
    }

    private void ensureSubscriberExists() {
        Response getResponse = RestAssured
                .given().baseUri(Constants.BACKEND_BASE_URL)
                .get("/v1/subscribers");

        assertSuccessful(getResponse, "GET /v1/subscribers");

        List<?> subscribers = getResponse.jsonPath().getList("$");
        if (subscribers.isEmpty()) {
            Response postResponse = RestAssured
                    .given().baseUri(Constants.BACKEND_BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(String.format(
                            "{\"id\":\"%s\",\"name\":\"%s\"}",
                            Constants.TEST_SUBSCRIBER_ID,
                            Constants.TEST_SUBSCRIBER_NAME))
                    .post("/v1/subscribers");

            assertSuccessful(postResponse, "POST /v1/subscribers");
        }
    }

    private void ensureAgentExists() {
        Response getResponse = RestAssured
                .given().baseUri(Constants.BACKEND_BASE_URL)
                .get("/v1/agents");

        assertSuccessful(getResponse, "GET /v1/agents");

        List<?> agents = getResponse.jsonPath().getList("$");
        if (agents.isEmpty()) {
            Response postResponse = RestAssured
                    .given().baseUri(Constants.BACKEND_BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(String.format(
                            "{\"code\":\"%s\",\"name\":\"%s\"}",
                            Constants.TEST_AGENT_CODE,
                            Constants.TEST_AGENT_NAME))
                    .post("/v1/agents");

            assertSuccessful(postResponse, "POST /v1/agents");
        }
    }

    private void assertSuccessful(Response response, String operation) {
        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new IllegalStateException(
                    "CatalogSetupHook: " + operation + " retornó " + status +
                    " — verificar que el backend está corriendo en " + Constants.BACKEND_BASE_URL);
        }
    }
}
