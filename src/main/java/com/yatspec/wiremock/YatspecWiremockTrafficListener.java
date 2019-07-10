package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

public class YatspecWiremockTrafficListener implements WiremockNetworkTrafficListener {

    private static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private static final String DEFAULT_TARGET = "Wiremock";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String END_OF_RESPONSE = "0";

    // overridden by test
    private TestState yatspec = new TestState();

    private AtomicInteger requestCount = new AtomicInteger();
    private StringBuilder requestBuilder = new StringBuilder();
    private StringBuilder responseBuilder = new StringBuilder();
    private String target = DEFAULT_TARGET;
    private int requestContentLength = 0;

    private static final Set<String> protocols = Set.of("DELETE", "GET", "POST", "PUT");

    //plantUml no like '/' or '_', so map to pretty name.
    private static final Map<String, String> targetNames = Map.of(
            "/api/v2/current_user", "AuthCurrentUser",
            "/ai-auth/v1/internal", "AuthInternal",
            "/orchestrator/v1/data_subject/patient/111222", "Healthcheck");

    void setYatspec(TestState interactions) {
        this.yatspec = interactions;
    }

    @Override
    public void opened(Socket socket) {
    }

    @Override
    public void incoming(Socket socket, ByteBuffer bytes) {
        String request = getBytes(bytes);
        requestBuilder.append(request);

        if (isStartOfRequest(request)) {
            target = extractTarget(request);
            requestContentLength = extractContentLength(request);

        }

        if (isEndOfRequest(request)) {
            requestCount.incrementAndGet();
            yatspec.log(format("request %s from App to %s", requestCount.get(), target),
                    requestBuilder.toString());

            requestBuilder = new StringBuilder();
        }
    }

    @Override
    public void outgoing(Socket socket, ByteBuffer bytes) {
        String response = getBytes(bytes);
        responseBuilder.append(response);

        if (isEndOfResponse(response)) {
            yatspec.log(format("response %s from %s to App", requestCount.get(), target),
                    responseBuilder.toString());

            responseBuilder = new StringBuilder();
            requestContentLength = 0;
        }
    }

    @Override
    public void closed(Socket socket) {
    }

    public void reset() {
        requestCount = new AtomicInteger();
        requestBuilder = new StringBuilder();
        responseBuilder = new StringBuilder();
    }

    private boolean isStartOfRequest(String request) {
        return protocols.stream().anyMatch(request::startsWith);
    }

    private boolean isEndOfRequest(String request) {
        if (!request.contains(CONTENT_LENGTH)) {
            return true;
        }

        //request with content length, might send request body separately, check last line is the body.
        String[] lines = request.trim().split(System.lineSeparator());
        String lastLine = lines[lines.length - 1].trim();
        return lastLine.length() == requestContentLength;
    }

    private boolean isEndOfResponse(String response) {
        return response.trim().replaceAll(System.lineSeparator(), "").equals(END_OF_RESPONSE);
    }

    private String extractTarget(String request) {
        String requestPath = request.split(System.lineSeparator(), 2)[0];
        String targetNamesKey = requestPath.split(" ")[1];
        return targetNames.getOrDefault(targetNamesKey, DEFAULT_TARGET);
    }

    private int extractContentLength(String request) {
        if (!request.contains(CONTENT_LENGTH)) {
            return 0;
        }

        int startIndex = request.indexOf(CONTENT_LENGTH) + CONTENT_LENGTH.length() + ": ".length();
        int endIndex = request.indexOf(System.lineSeparator(), startIndex);
        String contentLengthValue = request.substring(startIndex, endIndex).trim();
        return Integer.decode(contentLengthValue);
    }

    private String getBytes(ByteBuffer bytes) {
        try {
            return decoder.decode(bytes).toString();
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }
}
