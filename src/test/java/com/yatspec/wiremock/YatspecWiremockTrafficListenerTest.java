package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
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
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.googlecode.yatspec.sequence.Participants.ACTOR;
import static com.googlecode.yatspec.sequence.Participants.PARTICIPANT;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith({
        SpecListener.class,
        SequenceDiagramExtension.class
})
//todo assert on the sequence diagram?
class YatspecWiremockTrafficListenerTest implements WithTestState, WithParticipants {

    private static final int WIREMOCK_PORT = 8089;

    private static YatspecWiremockTrafficListener networkTrafficListener =
            new YatspecWiremockTrafficListener(
                    Map.of("/api/xxx", "apixxx",
                            "/api/xxx/111222", "apixxx")
            );

    private static WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration());

    private static WireMockConfiguration wireMockConfiguration() {
        return options()
                .port(WIREMOCK_PORT)
                //no chunking please (can't render it)
                .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER)
                .networkTrafficListener(networkTrafficListener);
//                .networkTrafficListener(new ConsoleNotifyingWiremockNetworkTrafficListener());
    }


    private TestState testState = new TestState();

    private Stubs stubs = new Stubs(wireMockServer);

    private Response when;

    @BeforeEach
    void setUp() {
        networkTrafficListener.setYatspec(testState);
        networkTrafficListener.reset();

        RestAssured.baseURI = "http://localhost:" + WIREMOCK_PORT;
    }

    @Test
    void captureTrafficOnGetWithNoBody() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .get("/api/xxx");

        thenAssertResponseCorrect();
    }

    @Test
    void captureTrafficOnGetWithBody() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"request value\"}")
                .get("/api/xxx");

        thenAssertResponseCorrect();
    }

    @Test
    void captureTrafficOnPostWithBody() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"request value\"}")
                .post("api/xxx/111222");

        thenAssertResponseCorrect();
    }

    @Test
    void captureTrafficOnUnmatchedRequestShouldDefaultToWiremock() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith().get("/api/yyy");

        thenResponse().statusCode(is(404));
    }

    @Test
    void captureTrafficOfMultipleSerialRequests() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"request value\"}")
                .post("api/xxx/111222");

        when = httpRequestWith().get("/api/yyy");

    }


    private RequestSpecification httpRequestWith() {
        return RestAssured.given();
    }

    private void thenAssertResponseCorrect() {
        thenResponse()
                .statusCode(is(200))
                .contentType(is("application/json; charset=utf-8"))
                .body("key", is("value"));
    }

    private ValidatableResponse thenResponse() {
        return when.then()
                .assertThat();
    }

    @AfterAll
    static void tearDown() {
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