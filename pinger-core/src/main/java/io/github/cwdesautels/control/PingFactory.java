package io.github.cwdesautels.control;

import io.github.cwdesautels.boundary.http.HttpPingClient;
import io.github.cwdesautels.domain.PingAggregate;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public final class PingFactory {
    private PingFactory() {
    }

    public static Ping<PingAggregate> maximumSaturation(List<URI> endpoints,
                                                        HttpClient client,
                                                        Duration timeout,
                                                        Duration period,
                                                        int concurrency,
                                                        int limit) {
        return new CompositePing(endpoints.stream()
                // http ping
                .map(endpoint -> new HttpPingClient(client, endpoint, timeout))
                // repeat ping n many times
                .map(delegate -> new ReplayingPing(delegate, limit))
                // enqueue n many pings
                .map(delegate -> new ConcurrentPing(delegate, concurrency))
                // sample pings
                .map(delegate -> new MetricPing(delegate, period))
                .collect(Collectors.toUnmodifiableList()));
    }
}
