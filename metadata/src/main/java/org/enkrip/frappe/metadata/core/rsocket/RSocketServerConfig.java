package org.enkrip.frappe.metadata.core.rsocket;

import java.util.List;

import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.rsocket.ipc.RequestHandlingRSocket;
import io.rsocket.ipc.SelfRegistrable;
import reactor.core.publisher.Mono;

@Configuration
public class RSocketServerConfig {

    @Bean
    public RequestHandlingRSocket requestHandlingRSocket(List<SelfRegistrable> selfRegistrableInstances) {
        RequestHandlingRSocket rSocket = new RequestHandlingRSocket();
        selfRegistrableInstances.forEach(rSocket::withEndpoint);
        return rSocket;
    }

    @Bean
    public RSocketServerCustomizer rSocketRPCServerCustomizer(RequestHandlingRSocket rSocket) {
        return rSocketServer -> rSocketServer.acceptor((setup, sendingSocket) -> Mono.just(rSocket));
    }

}
