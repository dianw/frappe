package org.enkrip.frappe.rest.core.rsocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Flux;

@Configuration
public class RSocketClientConfig {
    @Autowired
    private ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClient;

    @Bean
    public RSocket metadataLoadBalancedRSocket() {
        return buildLoadBalancedRSocket("frappe-metadata-server");
    }

    private RSocket buildLoadBalancedRSocket(String serviceId) {
        ReactiveLoadBalancer<ServiceInstance> chosen = loadBalancerClient.getInstance(serviceId);

        Flux<RSocket> rSockets = Flux.from(chosen.choose())
                .filter(Response::hasServer)
                .map(Response::getServer)
                .filter(instance -> instance.getMetadata().containsKey("rSocketPort"))
                .map(instance -> {
                    Map<String, String> metadata = instance.getMetadata();
                    int rSocketPort = Integer.parseInt(metadata.get("rSocketPort"));
                    return TcpClientTransport.create(instance.getHost(), rSocketPort);
                })
                .flatMap(RSocketConnector::connectWith);

        return new LoadBalancedRSocket(rSockets);
    }
}
