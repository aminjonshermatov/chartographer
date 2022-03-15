package com.shermatov.chartographer.handler;

import com.shermatov.chartographer.domain.Charta;
import com.shermatov.chartographer.exception.BadRequestException;
import com.shermatov.chartographer.exception.ChartaNotFoundException;
import com.shermatov.chartographer.exception.ServerErrorException;
import com.shermatov.chartographer.repository.ChartasConfigRepository;
import com.shermatov.chartographer.repository.ChartasRepository;
import com.shermatov.chartographer.repository.ImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static com.shermatov.chartographer.constants.ChartasConstants.*;

@Component
public class ChartasHandler {

    @Autowired
    ChartasConfigRepository chartasConfigRepository;

    @Autowired
    ChartasRepository chartasRepository;

    @Autowired
    ImagesRepository imagesRepository;


    public Mono<ServerResponse> createCharta(ServerRequest serverRequest) {
        Optional<String> widthOpt = serverRequest.queryParam("width");
        Optional<String> heightOpt = serverRequest.queryParam("height");

        if (widthOpt.isEmpty() || heightOpt.isEmpty())
            return Mono.error(BadRequestException::new);

        int width = Integer.parseInt(widthOpt.get());
        int height = Integer.parseInt(heightOpt.get());

        if (width <= 0 || width > MAX_IMAGE_WIDTH || height <= 0 || height > MAX_IMAGE_WIDTH)
            return Mono.error(BadRequestException::new);

        Charta charta = Charta.builder()
                .id(UUID.randomUUID().toString())
                .width(width)
                .height(height)
                .build();

        return chartasRepository.insert(charta)
                .flatMap(id -> chartasConfigRepository.getPathToContentFolder()
                        .flatMap(folder -> {
                            imagesRepository.createDefaultImage(Paths.get(folder, id + "." + IMAGE_FORMAT), width, height)
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();

                            return ServerResponse
                                    .status(HttpStatus.CREATED)
                                    .body(Mono.just(id), String.class);
                        }));
    }

    public Mono<ServerResponse> saveChartaFragment(ServerRequest serverRequest) {
        Optional<String> xOpt = serverRequest.queryParam("x");
        Optional<String> yOpt = serverRequest.queryParam("y");
        Optional<String> widthOpt = serverRequest.queryParam("width");
        Optional<String> heightOpt = serverRequest.queryParam("height");

        if (widthOpt.isEmpty() || heightOpt.isEmpty() || xOpt.isEmpty() || yOpt.isEmpty())
            return Mono.error(BadRequestException::new);

        int height = Integer.parseInt(heightOpt.get());
        int width = Integer.parseInt(widthOpt.get());
        int x = Integer.parseInt(xOpt.get());
        int y = Integer.parseInt(yOpt.get());
        if (x < 0 || y < 0 || x > MAX_IMAGE_WIDTH || y > MAX_IMAGE_HEIGHT)
            return Mono.error(BadRequestException::new);

        String id = serverRequest.pathVariable("id");

        return chartasRepository.findById(id)
                .switchIfEmpty(Mono.error(ChartaNotFoundException::new))
                .filter(charta -> x <= charta.getWidth() && y <= charta.getHeight())
                .switchIfEmpty(Mono.error(BadRequestException::new))
                .flatMap(charta -> chartasConfigRepository.getPathToContentFolder()
                        .flatMap(folderPath -> DataBufferUtils.join(serverRequest.bodyToFlux(DataBuffer.class))
                                .map(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);
                                    return new ByteArrayInputStream(bytes);
                                })
                                .map(istream -> {
                                    try {
                                        return ImageIO.read(istream);
                                    } catch (IOException ex) {
                                        throw Exceptions.propagate(ex);
                                    }
                                })
                                .filter(bImage -> bImage.getHeight() == height && bImage.getWidth() == width)
                                .switchIfEmpty(Mono.error(BadRequestException::new))
                                .flatMap(fragmentBF -> imagesRepository.overrideImage(
                                                Paths.get(folderPath, id + "." + IMAGE_FORMAT),
                                                fragmentBF,
                                                x,
                                                y,
                                                width,
                                                height
                                        )
                                        .switchIfEmpty(Mono.error(ServerErrorException::new))
                                        .flatMap(res -> ServerResponse.ok().build())
                                )));
}

    public Mono<ServerResponse> getCharta(ServerRequest serverRequest) {
        Optional<String> xOpt = serverRequest.queryParam("x");
        Optional<String> yOpt = serverRequest.queryParam("y");
        Optional<String> widthOpt = serverRequest.queryParam("width");
        Optional<String> heightOpt = serverRequest.queryParam("height");

        if (widthOpt.isEmpty() || heightOpt.isEmpty() || xOpt.isEmpty() || yOpt.isEmpty())
            return Mono.error(BadRequestException::new);

        int height = Integer.parseInt(heightOpt.get());
        int width = Integer.parseInt(widthOpt.get());

        if (width <= 0 || width > MAX_REQUEST_IMAGE_SIZE || height <= 0 || height > MAX_REQUEST_IMAGE_SIZE)
            return Mono.error(BadRequestException::new);

        int x = Integer.parseInt(xOpt.get());
        int y = Integer.parseInt(yOpt.get());
        if (x < 0 || y < 0 || x > MAX_IMAGE_WIDTH || y > MAX_IMAGE_HEIGHT)
            return Mono.error(BadRequestException::new);

        String id = serverRequest.pathVariable("id");
        return chartasRepository.findById(id)
                .switchIfEmpty(Mono.error(ChartaNotFoundException::new))
                .flatMap(charta -> {
                    if (x + width > charta.getWidth() || y + height > charta.getHeight())
                        return Mono.error(BadRequestException::new);
                    return chartasConfigRepository.getPathToContentFolder();
                })
                .flatMap(folderPath -> imagesRepository.getSubImage(Paths.get(folderPath, id + "." + IMAGE_FORMAT), x, y, width, height))
                .flatMap(image -> ServerResponse.ok()
                        .header("Content-Type", "image/" + IMAGE_FORMAT)
                        .body(BodyInserters.fromDataBuffers(Flux.just(image))));
    }

    public Mono<ServerResponse> deleteCharta(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return chartasRepository.deleteById(id)
                .switchIfEmpty(Mono.error(ChartaNotFoundException::new))
                .flatMap((ignore_) -> chartasConfigRepository.getPathToContentFolder())
                .flatMap(folderPath -> imagesRepository.deleteImage(Paths.get(folderPath, id + "." + IMAGE_FORMAT)))
                .flatMap((ignore_) -> ServerResponse.ok().build());
    }

}
