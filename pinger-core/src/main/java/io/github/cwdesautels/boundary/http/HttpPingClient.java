package io.github.cwdesautels.boundary.http;

import io.github.cwdesautels.control.Ping;
import io.github.cwdesautels.domain.ImmutablePingResult;
import io.github.cwdesautels.domain.PingResult;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.uri.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class HttpPingClient implements Ping<PingResult> {
    private static final Logger logger = LoggerFactory.getLogger(HttpPingClient.class);

    private final HttpClient client;
    private final URI endpoint;
    private final Duration timeout;

    public HttpPingClient(HttpClient client,
                          URI endpoint,
                          Duration timeout) {
        this.client = client.headers(headers -> headers.add(HttpHeaders.CACHE_CONTROL, "max-age=0, must-revalidate, no-store"));
        this.endpoint = endpoint;
        this.timeout = timeout;
    }

    @Override
    public Mono<PingResult> ping() {
        return client.get()
                .uri(UriBuilder.of(endpoint)
                        .queryParam("cb", ThreadLocalRandom.current().nextInt())
                        .build())
                .response()
                .timeout(timeout)
                .map(response -> (PingResult) ImmutablePingResult.of(endpoint, response.status().code()))
                .doOnNext(result -> logger.debug("{}", result))
                .doOnSubscribe(subscription -> logger.debug("HTTP GET request for endpoint: {}", endpoint))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
