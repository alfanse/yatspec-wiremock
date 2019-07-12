package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.googlecode.yatspec.junit.SequenceDiagramExtension;
import com.googlecode.yatspec.junit.SpecListener;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@ExtendWith({
        SpecListener.class,
        SequenceDiagramExtension.class
})
class YatspecWiremockReqestListenerTest extends YatspecWiremockTests {

    private static WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration());

    private static WireMockConfiguration wireMockConfiguration() {
        return options().port(WIREMOCK_PORT);
    }

    private YatspecWiremockReqestListener reqestListener = new YatspecWiremockReqestListener();

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + WIREMOCK_PORT;
        reqestListener.setYatspec(testState());
        wireMockServer.addMockServiceRequestListener(reqestListener);
    }


    @Override
    WireMockServer wireMockServer() {
        return wireMockServer;
    }
}