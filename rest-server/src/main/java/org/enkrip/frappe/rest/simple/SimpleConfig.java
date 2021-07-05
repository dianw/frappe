package org.enkrip.frappe.rest.simple;

import org.enkrip.frappe.metadata.SimpleServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.rsocket.RSocket;

@Configuration("simpleConfig2")
public class SimpleConfig {
    @Bean
    public SimpleServiceClient simpleServiceClient(RSocket rSocket) {
        return new SimpleServiceClient(rSocket);
    }
}
