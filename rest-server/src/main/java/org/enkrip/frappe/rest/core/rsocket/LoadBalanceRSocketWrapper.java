package org.enkrip.frappe.rest.core.rsocket;

import org.reactivestreams.Publisher;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.loadbalance.LoadbalanceRSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class LoadBalanceRSocketWrapper implements RSocket {
    private final LoadbalanceRSocketClient rSocketClient;

    LoadBalanceRSocketWrapper(LoadbalanceRSocketClient rSocketClient) {
        this.rSocketClient = rSocketClient;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return rSocketClient.source().flatMap(rSocket -> rSocket.fireAndForget(payload));
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return rSocketClient.source().flatMap(rSocket -> rSocket.requestResponse(payload));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return rSocketClient.source().flatMapMany(rSocket -> rSocket.requestStream(payload));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return rSocketClient.source().flatMapMany(rSocket -> rSocket.requestChannel(payloads));
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return rSocketClient.source().flatMap(rSocket -> rSocket.metadataPush(payload));
    }

    @Override
    public double availability() {
        return 1.0;
    }

    @Override
    public void dispose() {
        rSocketClient.dispose();
    }

    @Override
    public boolean isDisposed() {
        return rSocketClient.isDisposed();
    }

    @Override
    public Mono<Void> onClose() {
        return Mono.empty();
    }
}
