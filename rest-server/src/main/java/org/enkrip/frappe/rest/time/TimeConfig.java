package org.enkrip.frappe.rest.time;

import org.enkrip.frappe.metadata.time.TimeServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.rsocket.RSocket;

@Configuration
public class TimeConfig {
    @Bean
    public TimeServiceClient timeService(RSocket rSocket) {
        return new TimeServiceClient(rSocket);
    }
}
