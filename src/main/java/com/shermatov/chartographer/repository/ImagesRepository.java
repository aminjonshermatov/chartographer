package com.shermatov.chartographer.repository;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface ImagesRepository {

    Mono<Boolean> createDefaultImage(final Path filePath, int width, int height);
    Mono<Boolean> deleteImage(final Path filePath);
    Mono<DataBuffer> getSubImage(final Path filePath, int x, int y, int width, int height);
    Mono<Boolean> overrideImage(final Path source, BufferedImage fragment, int x, int y, int width, int height);

}
