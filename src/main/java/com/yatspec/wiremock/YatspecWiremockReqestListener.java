package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.lang.String.format;

public class YatspecWiremockReqestListener implements RequestListener {

    public static final String DEFAULT_TARGET = "Wiremock";

    private final Map<String, String> targetNames;
    private final Function<Request, String> requestFormatter;
    private final Function<Response, String> responseFormatter;

    private TestState yatspec;

    private AtomicInteger id = new AtomicInteger();

    public YatspecWiremockReqestListener(
            Map<String, String> targetNames,
            RequestFormatter requestFormatter,
            Function<Response, String> responseFormatter) {
        this.targetNames = targetNames;
        this.requestFormatter = requestFormatter;
        this.responseFormatter = responseFormatter;
    }

    /**
     * Reset instance with new TestState
     * @param yatspec
     */
    public void setYatspec(TestState yatspec) {
        this.yatspec = yatspec;
        this.id = new AtomicInteger();
    }

    @Override
    public void requestReceived(Request request, Response response) {
        String target = targetName(request);

        yatspec.log(format("request %s from App to %s", id.incrementAndGet(), target), requestFormatter.apply(request));

        yatspec.log(format("response %s from %s to App", id.get(), target), responseFormatter.apply(response));
    }

    private String targetName(Request request) {
        return targetNames.getOrDefault(request.getUrl(), DEFAULT_TARGET);
    }
}
