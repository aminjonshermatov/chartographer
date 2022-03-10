package com.shermatov.chartographer.handler;

import com.shermatov.chartographer.domain.Charta;
import com.shermatov.chartographer.exception.BadRequestException;
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

import static com.shermatov.chartographer.constants.ChartasConstants.*;

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
            return Mono.error(new BadRequestException());

        int width = Integer.parseInt(widthOpt.get());
        int height = Integer.parseInt(heightOpt.get());

        if (width <= 0 || width > MAX_IMAGE_WIDTH || height <= 0 || height > MAX_IMAGE_WIDTH)
            return Mono.error(new BadRequestException());

        return ServerResponse
                .status(HttpStatus.CREATED)
                .body(chartasRepository.insert(Charta.builder()
                        .id(UUID.randomUUID().toString())
                        .width(width)
                        .height(height)
                        .build()), String.class);
    }

    // /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
    public Mono<ServerResponse> getCharta(ServerRequest serverRequest) {
        Optional<String> xOpt = serverRequest.queryParam("x");
        Optional<String> yOpt = serverRequest.queryParam("y");
        Optional<String> widthOpt = serverRequest.queryParam("width");
        Optional<String> heightOpt = serverRequest.queryParam("height");

        if (widthOpt.isEmpty() || heightOpt.isEmpty() || xOpt.isEmpty() || yOpt.isEmpty())
            return Mono.error(new BadRequestException());

        int height = Integer.parseInt(heightOpt.get());
        int width = Integer.parseInt(widthOpt.get());

        if (width <= 0 || width > MAX_REQUEST_IMAGE_SIZE || height <= 0 || height > MAX_REQUEST_IMAGE_SIZE)
            return Mono.error(new BadRequestException());

        int x = Integer.parseInt(xOpt.get());
        int y = Integer.parseInt(yOpt.get());
        if (x < 0 || y < 0 || x > MAX_IMAGE_WIDTH || y > MAX_IMAGE_HEIGHT)
            return Mono.error(new BadRequestException());

        String id = serverRequest.pathVariable("id");
        return chartasRepository.findById(id)
                .flatMap(charta -> {
                    if (x > charta.getWidth() || y > charta.getHeight())
                        return Mono.error(new BadRequestException());

                    // TODO create methods to communicate with images
                    return Mono.empty();
                });
    }

    public Mono<ServerResponse> deleteCharta(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return chartasRepository.deleteById(id)
                .flatMap((ignore_) -> ServerResponse.ok().build());
    }

}
