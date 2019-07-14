package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import com.github.tomakehurst.wiremock.http.Response;

import java.util.function.Function;

public interface ResponseFormatter extends Function<Response, String> {

    @Override
    default String apply(Response response){
        LoggedResponse output = LoggedResponse.from(response);
        return Json.write(output);    
    }
}
