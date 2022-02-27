package io.github.cwdesautels.domain;

import io.micronaut.http.HttpStatus;
import org.immutables.value.Value;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Value.Immutable
public interface PingAggregate {
    @Value.Parameter
    URI endpoint();

    @Value.Parameter
    @Value.Auxiliary
    Map<HttpStatus, AtomicLong> metrics();

    @Value.Derived
    default long success() {
        return metrics()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getCode() >= 200 && entry.getKey().getCode() < 300)
                .map(entry -> entry.getValue().get())
                .reduce(Long::sum)
                .orElse(0L);
    }

    @Value.Derived
    default long error() {
        return metrics()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getCode() >= 300 || entry.getKey().getCode() < 200)
                .map(entry -> entry.getValue().get())
                .reduce(Long::sum)
                .orElse(0L);
    }

    default double total() {
        return success() + error();
    }

    @Value.Lazy
    default double ratio() {
        return total() / error();
    }
}
