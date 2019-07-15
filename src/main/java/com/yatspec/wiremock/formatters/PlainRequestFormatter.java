package com.yatspec.wiremock.formatters;

import com.github.tomakehurst.wiremock.http.Request;
import com.yatspec.wiremock.RequestFormatter;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlainRequestFormatter implements RequestFormatter {

    @Override
    public String apply(Request request) {
        return Stream.of(request.getMethod() + " " + request.getUrl(),
                request.getHeaders(),
                request.getBodyAsString())
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }
}
