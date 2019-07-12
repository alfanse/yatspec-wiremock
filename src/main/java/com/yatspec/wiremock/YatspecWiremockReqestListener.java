package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import com.googlecode.yatspec.state.givenwhenthen.WithTestState;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

public class YatspecWiremockReqestListener implements RequestListener, WithTestState {

    private TestState yatspec;

    private AtomicInteger id = new AtomicInteger();

    public void setYatspec(TestState yatspec) {
        this.yatspec = yatspec;
        id = new AtomicInteger();
    }

    @Override
    public void requestReceived(Request request, Response response) {
        LoggedRequest lRequest = LoggedRequest.createFrom(request);
        yatspec.log(format("request %s from App to %s", id.incrementAndGet(), targetName()), Json.write(lRequest));

        LoggedResponse lResponse = LoggedResponse.from(response);
        yatspec.log(format("response %s from %s to App", id.get(), targetName()), Json.write(lResponse));
    }

    private String targetName() {
        return "Wiremock";
    }

    @Override
    public TestState testState() {
        return yatspec;
    }
}
