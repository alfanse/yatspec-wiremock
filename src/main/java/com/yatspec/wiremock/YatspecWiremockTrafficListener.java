package com.yatspec.wiremock;

import com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import static java.lang.String.format;

public class YatspecWiremockTrafficListener implements WiremockNetworkTrafficListener {

    private static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private static final Set<String> protocols = Set.of("DELETE", "GET", "POST", "PUT");
    private static final String DEFAULT_TARGET = "Wiremock";
    private static final String CONTENT_LENGTH = "Content-Length";

    private TestState yatspec;

    private AtomicInteger id = new AtomicInteger();

    private AtomicInteger requestParts = new AtomicInteger();
    private AtomicInteger responseParts = new AtomicInteger();

    private String target = DEFAULT_TARGET;

    private final Map<String, String> targetNames;

    /**
     * @param targetNames - plantUml no like '/' or '_', so map to pretty name.
     *                    targetNames.startsWith comparison
     */
    public YatspecWiremockTrafficListener(Map<String, String> targetNames) {
        this.targetNames = targetNames;
    }

    //testState gets recreated each @Test and needs to be set here.
    void setYatspec(TestState interactions) {
        this.yatspec = interactions;
    }

    @Override
    public void opened(Socket socket) {
    }

    @Override
    public void incoming(Socket socket, ByteBuffer bytes) {
        String request = getBytes(bytes);

        if (isStartOfRequest(request)) {
            id.incrementAndGet();
            target = extractTarget(request);
            requestParts = new AtomicInteger();
            responseParts = new AtomicInteger();
        }

        yatspec.log(format("request %s_%s from App to %s", id.get(), requestParts.incrementAndGet(), target), request);
    }

    @Override
    public void outgoing(Socket socket, ByteBuffer bytes) {
        yatspec.log(format("response %s_%s from %s to App", id.get(), responseParts.incrementAndGet(), target),
                getBytes(bytes));
    }

    public void closed(Socket socket) {
    }

    public void reset() {
        id = new AtomicInteger();
        resetRequest();
    }

    private void resetRequest() {
        requestParts = new AtomicInteger();
    }

    private boolean isStartOfRequest(String request) {
        return protocols.stream().anyMatch(request::startsWith);
    }

    private String extractTarget(String request) {
        String requestPath = request.split(System.lineSeparator(), 2)[0];
        String targetNamesKey = requestPath.split(" ")[1];
        return targetNames.getOrDefault(targetNamesKey, DEFAULT_TARGET);
    }

    private String getBytes(ByteBuffer bytes) {
        try {
            return decoder.decode(bytes).toString();
        } catch (Exception e) {
            return getGzipBytes(bytes);
        }
    }

    private String getGzipBytes(ByteBuffer bytes) {
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes.array()));
            InputStreamReader reader = new InputStreamReader(gzip);
            BufferedReader in = new BufferedReader(reader);

            StringBuilder unpackedResponse = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                unpackedResponse.append(line);
            }

            return unpackedResponse.toString();

        } catch (Exception e) {
            return "Failed to un-gzip bytes, error: " + e.getMessage();
        }
    }
}
