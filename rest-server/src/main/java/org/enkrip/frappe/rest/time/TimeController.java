package org.enkrip.frappe.rest.time;

import java.time.Duration;

import org.enkrip.frappe.metadata.time.TimeServiceClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/metadata")
public class TimeController {
    private final TimeDataMapper timeDataMapper = TimeDataMapper.INSTANCE;
    private final TimeServiceClient timeService;

    public TimeController(TimeServiceClient timeService) {
        this.timeService = timeService;
    }

    @GetMapping(path = "/current-time-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GetCurrentTimeResponseData> getCurrentTimeStream() {
        return timeService.getCurrentTimeStream(Empty.newBuilder().build())
                .sample(Duration.ofSeconds(1))
                .map(timeDataMapper::toGetCurrentTimeResponseData);
    }

    @GetMapping(path = "/current-time")
    public Mono<GetCurrentTimeResponseData> getCurrentTime() {
        return timeService.getCurrentTime(Empty.newBuilder().build())
                .map(timeDataMapper::toGetCurrentTimeResponseData);
    }
}
