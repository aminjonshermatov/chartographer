package com.shermatov.chartographer.repository;

import com.shermatov.chartographer.domain.Pair;
import com.shermatov.chartographer.domain.Point;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface ImagesRepository {

    Mono<Boolean> createDefaultImage(final Path filePath, int width, int height);
    Mono<Boolean> deleteImage(final Path filePath);
    Mono<DataBuffer> getSubImage(final Path filePath, int x, int y, int width, int height);
    Mono<Boolean> overrideImage(final Path source, BufferedImage fragment, Flux<Pair<Point, Point>> points);

}
