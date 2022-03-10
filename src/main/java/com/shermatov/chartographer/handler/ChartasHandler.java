package com.shermatov.chartographer.handler;

import com.shermatov.chartographer.domain.Charta;
import com.shermatov.chartographer.errors.ErrorsBuilder;
import com.shermatov.chartographer.repository.ChartasConfigRepository;
import com.shermatov.chartographer.repository.ChartasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static com.shermatov.chartographer.constants.ChartasConstants.MAX_IMAGE_HEIGHT;
import static com.shermatov.chartographer.constants.ChartasConstants.MAX_IMAGE_WIDTH;

@Component
public class ChartasHandler {

    @Autowired
    ChartasConfigRepository chartasConfigRepository;

    @Autowired
    ChartasRepository chartasRepository;


    public Mono<ServerResponse> createCharta(ServerRequest serverRequest) {
        Optional<String> widthOpt = serverRequest.queryParam("width");
        Optional<String> heightOpt = serverRequest.queryParam("height");

        if (widthOpt.isEmpty() || heightOpt.isEmpty())
            return ErrorsBuilder.badRequest("Width and Height are required.");

        int width = Integer.parseInt(widthOpt.get());
        int height = Integer.parseInt(heightOpt.get());

        if (width <= 0 || width > MAX_IMAGE_WIDTH)
            return ErrorsBuilder.badRequest("Width must in (0, " + MAX_IMAGE_WIDTH + "] interval.");
        if (height <= 0 || height > MAX_IMAGE_WIDTH)
            return ErrorsBuilder.badRequest("Height must in (0, " + MAX_IMAGE_HEIGHT + "] interval.");

        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(chartasRepository.insert(Charta.builder()
                        .id(UUID.randomUUID().toString())
                        .width(width)
                        .height(height)
                        .build()), String.class);
    }

    public Mono<ServerResponse> getCharta(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return chartasRepository.findById(id)
                .flatMap(charta -> ServerResponse.ok().body(Mono.just(charta), Charta.class));
    }

    public Mono<ServerResponse> deleteCharta(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return chartasRepository.deleteById(id)
                .flatMap((ignore_) -> ServerResponse.ok().build());
    }

}
