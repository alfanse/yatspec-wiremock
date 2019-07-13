package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import java.util.function.Function;

public interface RequestFormatter extends Function<Request, String> {

    @Override
    default String apply(Request request){
        LoggedRequest output = LoggedRequest.createFrom(request);
        return Json.write(output);
    }
}
