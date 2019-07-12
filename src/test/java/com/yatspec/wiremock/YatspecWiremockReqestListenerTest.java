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
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith({
        SpecListener.class,
        SequenceDiagramExtension.class
})
class YatspecWiremockReqestListenerTest implements WithTestState, WithParticipants {

    private static final int WIREMOCK_PORT = 8089;

    private static WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration());

    private static WireMockConfiguration wireMockConfiguration() {
        return options()
                .port(WIREMOCK_PORT);
    }

    private TestState testState = new TestState();

    private YatspecWiremockReqestListener reqestListener = new YatspecWiremockReqestListener();

    private Stubs stubs = new Stubs(wireMockServer);

    private Response when;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + WIREMOCK_PORT;
        reqestListener.setYatspec(testState);
        wireMockServer.addMockServiceRequestListener(reqestListener);
    }


    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }


    @Test
    void name() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"request value\"}")
                .get("/api/xxx");

        thenResponse().statusCode(is(200));
    }

    @Override
    public List<Participant> participants() {
        return null;
    }

    @Override
    public TestState testState() {
        return testState;
    }

    private RequestSpecification httpRequestWith() {
        return RestAssured.given();
    }


    private ValidatableResponse thenResponse() {
        return when.then()
                .assertThat();
    }



}