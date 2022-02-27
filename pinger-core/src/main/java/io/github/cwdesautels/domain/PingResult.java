package io.github.cwdesautels.domain;

import io.micronaut.http.HttpStatus;
import org.immutables.value.Value;

import java.net.URI;

@Value.Immutable
public interface PingResult {
    @Value.Parameter
    URI endpoint();

    @Value.Parameter
    int code();

    @Value.Lazy
    default HttpStatus status() {
        return HttpStatus.valueOf(code());
    }
}
