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
import io.restassured.specification.RequestSpecification;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.googlecode.yatspec.sequence.Participants.ACTOR;
import static com.googlecode.yatspec.sequence.Participants.PARTICIPANT;

@ExtendWith({
        SpecListener.class,
        SequenceDiagramExtension.class
})
//todo assert on the sequence diagram?
class YatspecWiremockTrafficListenerTest implements WithTestState, WithParticipants {

    private static final int WIREMOCK_PORT = 8089;

    private static YatspecWiremockTrafficListener networkTrafficListener =
            new YatspecWiremockTrafficListener(
                    Map.of("/api/xxx", "apixxx"));

    private static WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration());

    private static WireMockConfiguration wireMockConfiguration() {
        return options()
                .port(WIREMOCK_PORT)
                .networkTrafficListener(networkTrafficListener);
    }


    private TestState testState = new TestState();

    private Response when;

    @BeforeEach
    void setUp() {
        networkTrafficListener.setYatspec(testState);
        networkTrafficListener.reset();

        RestAssured.baseURI = "http://localhost:" + WIREMOCK_PORT;
    }

    @Test
    void captureTrafficOnSingleGetRequest() {
        givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .get("/api/xxx");

        thenAssertResponseCorrect();
    }

    @Test
    void captureTrafficOnMultipartGetRequest() {
        givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"response value\"}")
                .get("/api/xxx");

        thenAssertResponseCorrect();
    }

    private RequestSpecification httpRequestWith() {
        return RestAssured.given();
    }

    private void givenWiremockStubsAvailable() {
        wireMockServer.stubFor(
                get(urlPathMatching("/api/xxx"))
                        .withHeader("Authorization", equalTo("Bearer sometoken"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json; charset=utf-8")
                                        .withBody("{\"key\":\"value\"}")
                                        .withStatus(200)));

        wireMockServer.start();
    }

    private void thenAssertResponseCorrect() {
        when.then()
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
        return List.of(
                ACTOR.create("App", "RestAssured"),
                PARTICIPANT.create("apixxx", "Api XXX")
        );
    }

    @Override
    public TestState testState() {
        return testState;
    }
}