package io.github.cwdesautels.control;

import io.github.cwdesautels.domain.ImmutablePingAggregate;
import io.github.cwdesautels.domain.PingAggregate;
import io.github.cwdesautels.domain.PingResult;
import io.micronaut.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricPing implements Ping<PingAggregate> {
    private static final Logger logger = LoggerFactory.getLogger(MetricPing.class);

    private final Ping<PingResult> delegate;
    private final Duration period;

    public MetricPing(Ping<PingResult> delegate,
                      Duration period) {
        this.delegate = delegate;
        this.period = period;
    }

    @Override
    public Flux<PingAggregate> ping() {
        final int size = HttpStatus.values().length * 2;
        final Map<URI, Map<HttpStatus, AtomicLong>> container = new ConcurrentHashMap<>(64);

        return Flux.from(delegate.ping())
                .map(result -> {
                    final Map<HttpStatus, AtomicLong> metrics = container.computeIfAbsent(result.endpoint(), key -> new ConcurrentHashMap<>(size));

                    metrics.computeIfAbsent(result.status(), key -> new AtomicLong(0)).addAndGet(1);

                    return result.endpoint();
                })
                .sample(period)
                .map(endpoint -> (PingAggregate) ImmutablePingAggregate.of(endpoint, container.getOrDefault(endpoint, Collections.emptyMap())))
                .doOnNext(result -> logger.info("HTTP Get stats S={} E={} T={} @ {}% for endpoint: {}", result.success(), result.error(), result.total(), result.ratio(), result.endpoint()));
    }
}
