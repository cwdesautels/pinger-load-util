package io.github.cwdesautels.control;

import org.reactivestreams.Publisher;

public interface Ping<O> {
    Publisher<O> ping();
}
