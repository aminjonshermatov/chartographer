package com.shermatov.chartographer.repository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;

public interface ImagesRepository {

    Mono<Void> createDefaultImage(final String folder, final String id, int width, int height);
    Mono<BufferedImage> getImage(final String imagePath);
    Mono<DataBuffer> overrideImage(Mono<Resource> image, int x, int y, int width, int height);

}
