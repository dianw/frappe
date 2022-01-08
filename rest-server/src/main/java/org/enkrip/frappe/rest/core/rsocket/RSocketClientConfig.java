package org.enkrip.frappe.rest.core.rsocket;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.loadbalance.LoadbalanceRSocketClient;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class RSocketClientConfig {
    private static final String DISCOVERY_METADATA_RSOCKET_PORT = "rsocket-port";

    @Bean
    public RSocket rSocket(RSocketClient client) {
        return new LoadBalancedRSocket(client);
    }

    @Bean(destroyMethod = "dispose")
    public RSocketClient rSocketClient(ReactiveDiscoveryClient discoveryClient) {
        return LoadbalanceRSocketClient.create(RSocketConnector.create(),
                Flux.interval(Duration.ZERO, Duration.ofSeconds(15))
                        .flatMap(i -> getRSocketLoadBalancedTargets(discoveryClient))
                        .onBackpressureLatest()
                        .cache(Duration.ofSeconds(15))
        );
    }

    private Mono<List<LoadbalanceTarget>> getRSocketLoadBalancedTargets(ReactiveDiscoveryClient discoveryClient) {
        return discoveryClient.getInstances("frappe-metadata-server")
                .filter(instance -> instance.getMetadata().containsKey(DISCOVERY_METADATA_RSOCKET_PORT))
                .map(instance -> {
                    Map<String, String> metadata = instance.getMetadata();
                    int rSocketPort = Integer.parseInt(metadata.get(DISCOVERY_METADATA_RSOCKET_PORT));
                    TcpClientTransport clientTransport = TcpClientTransport.create(instance.getHost(), rSocketPort);
                    return LoadbalanceTarget.from(instance.getHost() + rSocketPort, clientTransport);
                })
                .collectList();
    }
}
