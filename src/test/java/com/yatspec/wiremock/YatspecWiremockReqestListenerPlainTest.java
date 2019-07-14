package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import com.googlecode.yatspec.state.givenwhenthen.WithTestState;
import com.yatspec.wiremock.formatters.PlainRequestFormatter;
import com.yatspec.wiremock.formatters.PlainResponseFormatter;
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
            new PlainRequestFormatter(),
            new PlainResponseFormatter());

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

}