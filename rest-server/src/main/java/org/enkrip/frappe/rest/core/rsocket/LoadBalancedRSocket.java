package org.enkrip.frappe.rest.core.rsocket;

import org.reactivestreams.Publisher;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LoadBalancedRSocket implements RSocket {
    private final RSocketClient rSocketClient;

    public LoadBalancedRSocket(RSocketClient rSocketClient) {
        this.rSocketClient = rSocketClient;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return rSocketClient.fireAndForget(Mono.just(payload));
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return rSocketClient.requestResponse(Mono.just(payload));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return rSocketClient.requestStream(Mono.just(payload));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return rSocketClient.requestChannel(payloads);
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return rSocketClient.metadataPush(Mono.just(payload));
    }

    @Override
    public void dispose() {
        rSocketClient.dispose();
    }

    @Override
    public boolean isDisposed() {
        return rSocketClient.isDisposed();
    }
}
