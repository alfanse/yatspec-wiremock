package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.googlecode.yatspec.sequence.Participants.ACTOR;
import static com.googlecode.yatspec.sequence.Participants.PARTICIPANT;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith({
        SpecListener.class,
        SequenceDiagramExtension.class
})
//todo assert on the sequence diagram?
abstract class YatspecWiremockTests implements WithTestState, WithParticipants {

    static final int WIREMOCK_PORT = 8089;

    abstract WireMockServer wireMockServer();

    private TestState testState = new TestState();

    private Stubs stubs = new Stubs(wireMockServer());

    private Response when;

    @BeforeAll
    static void initaliseRestAssured() {
        RestAssured.baseURI = "http://localhost:" + WIREMOCK_PORT;
    }

    @Override
    public TestState testState() {
        return testState;
    }

    @Override
    public List<Participant> participants() {
        return List.of(
                ACTOR.create("App", "RestAssured"),
                PARTICIPANT.create("apixxx", "Api XXX")
        );
    }

    @AfterEach
    void tearDown() {
        wireMockServer().stop();
    }

    @Test
    public void captureTrafficOnGetWithNoBody() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .get("/api/xxx");

        thenAssertResponseCorrect();
    }

    @Test
    public void captureTrafficOnGetWithBody() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"request value\"}")
                .get("/api/xxx");

        thenAssertResponseCorrect();
    }

    @Test
    public void captureTrafficOnPostWithBody() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith()
                .header("Authorization", "Bearer sometoken")
                .contentType(ContentType.JSON)
                .body("{\"requestKey\":\"request value\"}")
                .post("api/xxx/111222");

        thenAssertResponseCorrect();
    }

    @Test
    public void captureTrafficOnUnmatchedRequestShouldDefaultToWiremock() {
        stubs.givenWiremockStubsAvailable();

        when = httpRequestWith().get("/api/yyy");

        thenResponse().statusCode(is(404));
    }

    @Test
    public void captureTrafficOfMultipleSerialRequests() {
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
}