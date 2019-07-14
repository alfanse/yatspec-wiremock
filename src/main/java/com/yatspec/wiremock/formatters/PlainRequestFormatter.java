package com.yatspec.wiremock.formatters;

import com.github.tomakehurst.wiremock.http.Request;
import com.yatspec.wiremock.RequestFormatter;

public class PlainRequestFormatter implements RequestFormatter {
    @Override
    public String apply(Request request) {
        return addIfPresent(request.getMethod() + " " + request.getUrl())
                + addIfPresent(request.getHeaders())
                + addIfPresent(request.getBodyAsString());
    }

    private String addIfPresent(Object addme) {
        return addme == null ? "" : addme.toString() + " \n";
    }
}
