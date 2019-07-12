package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

public class YatspecWiremockReqestListener implements RequestListener {

    public static final String DEFAULT_TARGET = "Wiremock";

    private final Map<String, String> targetNames;

    private TestState yatspec;

    private AtomicInteger id = new AtomicInteger();

    public YatspecWiremockReqestListener(Map<String, String> targetNames) {
        this.targetNames = targetNames;
    }

    public void setYatspec(TestState yatspec) {
        this.yatspec = yatspec;
        id = new AtomicInteger();
    }

    @Override
    public void requestReceived(Request request, Response response) {
        String target = targetName(request);
        LoggedRequest lRequest = LoggedRequest.createFrom(request);
        yatspec.log(format("request %s from App to %s", id.incrementAndGet(), target), Json.write(lRequest));

        LoggedResponse lResponse = LoggedResponse.from(response);
        yatspec.log(format("response %s from %s to App", id.get(), target), Json.write(lResponse));
    }

    private String targetName(Request request) {
        return targetNames.getOrDefault(request.getUrl(), DEFAULT_TARGET);
    }
}
