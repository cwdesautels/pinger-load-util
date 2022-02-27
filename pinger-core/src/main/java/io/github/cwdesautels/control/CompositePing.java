package io.github.cwdesautels.control;

import io.github.cwdesautels.domain.PingAggregate;
import reactor.core.publisher.Flux;

import java.util.List;

public class CompositePing implements Ping<PingAggregate> {
    private final List<Ping<PingAggregate>> delegates;

    public CompositePing(List<Ping<PingAggregate>> delegates) {
        this.delegates = delegates;
    }

    @Override
    public Flux<PingAggregate> ping() {
        return Flux.fromIterable(delegates).flatMap(Ping::ping);
    }
}
