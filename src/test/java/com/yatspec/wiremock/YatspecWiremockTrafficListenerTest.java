package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import com.googlecode.yatspec.state.givenwhenthen.WithTestState;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class YatspecWiremockTrafficListenerTest extends YatspecWiremockTests implements WithTestState {

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

    @Override
    public TestState testState() {
        return testState;
    }

    @Override
    WireMockServer wireMockServer() {
        return wireMockServer;
    }

    @BeforeEach
    void setUp() {
        networkTrafficListener.setYatspec(testState);
        networkTrafficListener.reset();
    }
}