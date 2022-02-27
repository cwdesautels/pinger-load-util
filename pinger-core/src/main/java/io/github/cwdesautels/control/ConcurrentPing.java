package io.github.cwdesautels.control;

import io.github.cwdesautels.domain.PingResult;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class ConcurrentPing implements Ping<PingResult> {
    private final Ping<PingResult> delegate;
    private final long concurrency;

    public ConcurrentPing(Ping<PingResult> delegate,
                          long concurrency) {
        this.delegate = delegate;
        this.concurrency = concurrency;
    }

    @Override
    public Flux<PingResult> ping() {
        return Flux.fromStream(Stream.generate(() -> delegate)
                        .limit(concurrency))
                .flatMap(Ping::ping);
    }
}
