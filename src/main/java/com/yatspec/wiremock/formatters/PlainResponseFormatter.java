package com.yatspec.wiremock.formatters;

import com.github.tomakehurst.wiremock.http.Response;
import com.yatspec.wiremock.ResponseFormatter;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlainResponseFormatter implements ResponseFormatter {
    @Override
    public String apply(Response response) {
        return Stream.of(
                response.getStatus(),
                response.getStatusMessage(),
                response.getHeaders(),
                response.getBodyAsString())
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }
}
