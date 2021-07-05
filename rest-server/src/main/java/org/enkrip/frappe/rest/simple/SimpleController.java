package org.enkrip.frappe.rest.simple;

import org.enkrip.frappe.metadata.SimpleRequest;
import org.enkrip.frappe.metadata.SimpleResponse;
import org.enkrip.frappe.metadata.SimpleServiceClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/simple")
public class SimpleController {
    private final SimpleServiceClient simpleServiceClient;

    public SimpleController(SimpleServiceClient simpleServiceClient) {
        this.simpleServiceClient = simpleServiceClient;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> simple(@RequestParam(name = "text", defaultValue = "simple") String text) {
        return simpleServiceClient
                .requestStrm(SimpleRequest
                        .newBuilder()
                        .setRequestMessage(text)
                        .build())
                .map(SimpleResponse::getResponseMessage);
    }
}
