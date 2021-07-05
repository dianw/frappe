package org.enkrip.frappe.rest.core.rsocket;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.rsocket.RSocket;
import io.rsocket.loadbalance.LoadbalanceRSocketClient;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;

@Configuration
public class RSocketClientConfig {

    @Bean
    public LoadbalanceRSocketClient loadbalanceRSocketClient(DiscoveryClient discoveryClient) {
        List<ServiceInstance> instances = discoveryClient.getInstances("frappe-metadata-server");
        List<LoadbalanceTarget> targets = instances.stream()
                .filter(instance -> instance.getMetadata().containsKey("rSocketPort"))
                .map(instance -> {
                    Map<String, String> metadata = instance.getMetadata();
                    int rSocketPort = Integer.parseInt(metadata.get("rSocketPort"));
                    ClientTransport clientTransport = TcpClientTransport.create(instance.getHost(), rSocketPort);
                    return LoadbalanceTarget.from(instance.getInstanceId(), clientTransport);
                })
                .collect(Collectors.toList());

        return LoadbalanceRSocketClient
                .builder(s -> s.onNext(targets))
                .roundRobinLoadbalanceStrategy()
                .build();
    }

    @Bean
    public RSocket loadBalanceRSocketWrapper(LoadbalanceRSocketClient loadbalanceRSocketClient) {
        return new LoadBalanceRSocketWrapper(loadbalanceRSocketClient);
    }

}
