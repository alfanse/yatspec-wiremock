package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.googlecode.yatspec.junit.SequenceDiagramExtension;
import com.googlecode.yatspec.junit.SpecListener;
import com.googlecode.yatspec.junit.WithParticipants;
import com.googlecode.yatspec.sequence.Participant;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import com.googlecode.yatspec.state.givenwhenthen.WithTestState;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@ExtendWith({
        SpecListener.class,
        SequenceDiagramExtension.class
})
class YatspecWiremockTrafficListenerTest implements WithTestState, WithParticipants {

    public static final int WIREMOCK_PORT = 8089;

    private static YatspecWiremockTrafficListener networkTrafficListener =
            new YatspecWiremockTrafficListener();

    private static WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration());
    private Response response;

    private static WireMockConfiguration wireMockConfiguration() {
        return options()
                .port(WIREMOCK_PORT)
                .networkTrafficListener(networkTrafficListener);
    }

    private TestState testState = new TestState();

    @BeforeEach
    void setUp() {
        networkTrafficListener.setYatspec(testState);
        RestAssured.baseURI = "http://localhost:" + WIREMOCK_PORT;
    }

    @Test
    void captureTrafficOnSingleGetRequest() {
        stubGetWithJsonResponse();

        wireMockServer.start();

        response =  RestAssured.given()
                .header("Authorization", "Bearer sometoken")
                .get("/api/xxx");

        thenAssertResponseCorrect();

        //todo assert on the sequence diagram?
    }

    @Test
    void captureTrafficOnMultipartGetRequest() {
        stubGetWithJsonResponse();

        wireMockServer.start();

        response =  RestAssured.given()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"response value\"}")
                .get("/api/xxx");

        thenAssertResponseCorrect();

        //todo assert on the sequence diagram?
    }

    private void stubGetWithJsonResponse() {
        wireMockServer.stubFor(
                get(urlPathMatching("/api/xxx"))
                        .withHeader("Authorization", equalTo("Bearer sometoken"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json; charset=utf-8")
                                        .withBody("{\"key\":\"value\"}")
                                        .withStatus(200)));
    }

    private void thenAssertResponseCorrect() {
        response.then()
                .assertThat()
                .statusCode(CoreMatchers.is(200))
                .contentType(CoreMatchers.is("application/json; charset=utf-8"))
                .body("key", CoreMatchers.is("value"));
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Override
    public List<Participant> participants() {
        return null;
    }

    @Override
    public TestState testState() {
        return testState;
    }
}