package org.enkrip.frappe.rest.core.rsocket;

import java.time.Duration;

import org.reactivestreams.Publisher;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.loadbalance.LoadbalanceRSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class LoadBalanceRSocketWrapper implements RSocket {
    private final LoadbalanceRSocketClient rSocketClient;
    private final Duration timeout;

    LoadBalanceRSocketWrapper(LoadbalanceRSocketClient rSocketClient) {
        this(rSocketClient, 3000);
    }

    LoadBalanceRSocketWrapper(LoadbalanceRSocketClient rSocketClient, long timeoutMillis) {
        this.rSocketClient = rSocketClient;
        this.timeout = Duration.ofMillis(timeoutMillis);
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return rSocketClient.source()
                .flatMap(rSocket -> rSocket.fireAndForget(payload).timeout(timeout));
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return rSocketClient.source()
                .flatMap(rSocket -> rSocket.requestResponse(payload).timeout(timeout));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return rSocketClient.source()
                .flatMapMany(rSocket -> rSocket.requestStream(payload).timeout(timeout));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return rSocketClient.source()
                .flatMapMany(rSocket -> rSocket.requestChannel(payloads).timeout(timeout));
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return rSocketClient.source()
                .flatMap(rSocket -> rSocket.metadataPush(payload).timeout(timeout));
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
