package org.enkrip.frappe.rest.core.rsocket;

import org.reactivestreams.Publisher;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LoadBalancedRSocket implements RSocket {
    private final Flux<RSocket> source;

    public LoadBalancedRSocket(Flux<RSocket> source) {
        this.source = source;
    }


    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return source.flatMap(rSocket -> rSocket.fireAndForget(payload)).next();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return source.flatMap(rSocket -> rSocket.requestResponse(payload)).next();
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return source.flatMap(rSocket -> rSocket.requestStream(payload));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return source.flatMap(rSocket -> rSocket.requestChannel(payloads));
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return source.flatMap(rSocket -> rSocket.metadataPush(payload)).next();
    }
}
