package org.enkrip.frappe.rest.time;

import java.time.Duration;

import org.enkrip.frappe.metadata.time.TimeServiceClient;
import org.enkrip.frappe.rest.openapi.api.TimeApi;
import org.enkrip.frappe.rest.openapi.model.GetCurrentTimeResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.google.protobuf.Empty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class TimeController implements TimeApi {
    private final TimeDataMapper timeDataMapper = TimeDataMapper.INSTANCE;
    private final TimeServiceClient timeService;

    public TimeController(TimeServiceClient timeService) {
        this.timeService = timeService;
    }

    @Override
    public Mono<GetCurrentTimeResponseData> timeCurrentGet(ServerWebExchange exchange) {
        return timeService.getCurrentTime(Empty.newBuilder().build())
                .map(timeDataMapper::toGetCurrentTimeResponseData);
    }

    @Override
    public Flux<GetCurrentTimeResponseData> timeCurrentStreamGet(ServerWebExchange exchange) {
        return timeService.getCurrentTimeStream(Empty.newBuilder().build())
                .sample(Duration.ofSeconds(1))
                .map(timeDataMapper::toGetCurrentTimeResponseData);
    }
}
