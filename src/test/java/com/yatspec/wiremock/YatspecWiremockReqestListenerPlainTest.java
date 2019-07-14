package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import com.googlecode.yatspec.state.givenwhenthen.WithTestState;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class YatspecWiremockReqestListenerPlainTest extends YatspecWiremockTests implements WithTestState {

    private WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration());

    private static WireMockConfiguration wireMockConfiguration() {
        return options().port(WIREMOCK_PORT);
    }

    private YatspecWiremockReqestListener reqestListener = new YatspecWiremockReqestListener(
            Map.of("/api/xxx", "apixxx",
                    "/api/xxx/111222", "apixxx"),
            new RequestFormatter() {
                @Override
                public String apply(Request request) {
                    return addIfPresent(request.getMethod() + " " + request.getUrl())
                            + addIfPresent(request.getHeaders())
                            + addIfPresent(request.getBodyAsString());
                }
            },
            new ResponseFormatter() {
                @Override
                public String apply(Response response) {

                    return response.getStatus() + " " +addIfPresent(response.getStatusMessage())
                            + addIfPresent(response.getHeaders())
                            + addIfPresent(response.getBodyAsString());
                }
            });

    private TestState testState = new TestState();

    @Override
    public TestState testState() {
        return testState;
    }

    @BeforeEach
    void setUp() {
        reqestListener.setYatspec(testState());
        wireMockServer.addMockServiceRequestListener(reqestListener);
    }

    @Override
    WireMockServer wireMockServer() {
        return wireMockServer;
    }

    private String addIfPresent(Object addme) {
        return addme == null ? "" : addme.toString() + " \n";
    }
}