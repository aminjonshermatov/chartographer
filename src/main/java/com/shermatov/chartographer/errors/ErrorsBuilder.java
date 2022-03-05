package com.shermatov.chartographer.errors;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ErrorsBuilder {

    public static Mono<ServerResponse> badRequest(String errorMessage) {
        return ServerResponse.badRequest().body(Mono.just(errorMessage), String.class);
    }

}
