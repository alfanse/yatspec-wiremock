package com.yatspec.wiremock.formatters;

import com.github.tomakehurst.wiremock.http.Response;
import com.yatspec.wiremock.ResponseFormatter;

public class PlainResponseFormatter implements ResponseFormatter {

    @Override
    public String apply(Response response) {

        return response.getStatus() + " " +addIfPresent(response.getStatusMessage())
                + addIfPresent(response.getHeaders())
                + addIfPresent(response.getBodyAsString());
    }

    private String addIfPresent(Object addme) {
        return addme == null ? "" : addme.toString() + " \n";
    }
}
