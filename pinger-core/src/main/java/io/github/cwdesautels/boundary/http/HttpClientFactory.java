package io.github.cwdesautels.boundary.http;

import io.netty.handler.logging.LogLevel;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;

public final class HttpClientFactory {
    private HttpClientFactory() {
    }

    public static HttpClient client() {
        return HttpClient.create()
                .compress(true)
                .disableRetry(true)
                .followRedirect(false)
                .wiretap(HttpClientFactory.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8)
                .keepAlive(true)
                .secure()
                .noProxy();
    }
}
