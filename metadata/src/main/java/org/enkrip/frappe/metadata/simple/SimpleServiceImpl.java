package org.enkrip.frappe.metadata.simple;

import java.time.Duration;

import org.enkrip.frappe.metadata.SimpleRequest;
import org.enkrip.frappe.metadata.SimpleResponse;
import org.enkrip.frappe.metadata.SimpleService;
import org.enkrip.frappe.metadata.SimpleServiceServer;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SimpleServiceImpl implements SimpleService {

    @Override
    public Mono<SimpleResponse> requestResp(SimpleRequest message, ByteBuf metadata) {
        SimpleServiceServer server;
        return null;
    }

    @Override
    public Mono<Empty> fireAndForg(SimpleRequest message, ByteBuf metadata) {
        return null;
    }

    @Override
    public Flux<SimpleResponse> requestStrm(SimpleRequest message, ByteBuf metadata) {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> SimpleResponse.newBuilder().setResponseMessage(message.getRequestMessage()).build());
    }

    @Override
    public Mono<SimpleResponse> streamingRequestSingleResp(Publisher<SimpleRequest> messages, ByteBuf metadata) {
        return null;
    }

    @Override
    public Flux<SimpleResponse> streamingRequestAndResp(Publisher<SimpleRequest> messages, ByteBuf metadata) {
        return null;
    }
}
