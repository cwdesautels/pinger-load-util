package io.github.cwdesautels.control;

import io.github.cwdesautels.domain.PingResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReplayingPing implements Ping<PingResult> {
    private final Ping<PingResult> delegate;
    private final long replays;

    public ReplayingPing(Ping<PingResult> delegate,
                         long replays) {
        this.delegate = delegate;
        this.replays = replays;
    }

    @Override
    public Flux<PingResult> ping() {
        return Mono.from(delegate.ping()).repeat(replays);
    }
}
