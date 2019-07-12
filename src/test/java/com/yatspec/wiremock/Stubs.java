package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Stubs {
    private WireMockServer wireMockServer;

    public Stubs(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    void givenWiremockStubsAvailable() {
        wireMockServer.stubFor(
                get(urlPathMatching("/api/xxx"))
                        .withHeader("Authorization", equalTo("Bearer sometoken"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json; charset=utf-8")
                                        .withBody("{\"key\":\"value\"}")
                                        .withStatus(200)));

        wireMockServer.stubFor(
                post(urlPathMatching("/api/xxx/111222"))
                        .withHeader("Authorization", equalTo("Bearer sometoken"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json; charset=utf-8")
                                        .withBody("{\"key\":\"value\"}")
                                        .withStatus(200)));

        wireMockServer.start();
    }
}
