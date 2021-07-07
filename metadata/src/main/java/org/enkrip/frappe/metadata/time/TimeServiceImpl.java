package org.enkrip.frappe.metadata.time;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public Flux<GetCurrentTimeResponse> getCurrentTimeStream(Empty message, ByteBuf metadata) {
        return Flux.interval(Duration.ZERO, Duration.ofMillis(500))
                .flatMap(i -> getCurrentTime(message, metadata));
    }

    @Override
    public Mono<GetCurrentTimeResponse> getCurrentTime(Empty message, ByteBuf metadata) {
        return Mono.just(GetCurrentTimeResponse
                .newBuilder()
                .setCurrentTimeIso(Instant.now().toString())
                .build());
    }
}
