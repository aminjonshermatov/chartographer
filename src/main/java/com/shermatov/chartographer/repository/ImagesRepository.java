package com.shermatov.chartographer.repository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;

public interface ImagesRepository {

    Mono<Boolean> createDefaultImage(final String folder, final String id, int width, int height);
    Mono<Boolean> deleteImage(final String folder, final String id);
    Mono<DataBuffer> getSubImage(final String folder, final String id, int x, int y, int width, int height);
    Mono<DataBuffer> overrideImage(Mono<Resource> image, int x, int y, int width, int height);

}
