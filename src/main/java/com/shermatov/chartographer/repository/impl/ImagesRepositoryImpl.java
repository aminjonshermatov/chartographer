package com.shermatov.chartographer.repository.impl;

import com.shermatov.chartographer.exception.ServerErrorException;
import com.shermatov.chartographer.repository.ImagesRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.shermatov.chartographer.constants.ChartasConstants.DEFAULT_COLOR;
import static com.shermatov.chartographer.constants.ChartasConstants.IMAGE_FORMAT;

@Repository
public class ImagesRepositoryImpl implements ImagesRepository {

    @Override
    public Mono<Boolean> createDefaultImage(final Path filePath, int width, int height) {
        return Mono.defer(() -> {
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();

            graphics2D.setColor(DEFAULT_COLOR);
            graphics2D.fillRect(0, 0, width, height);

            try {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);

                ImageIO.write(bufferedImage, IMAGE_FORMAT, new BufferedOutputStream(Files.newOutputStream(filePath)));
            } catch (IOException e) {
                return Mono.error(ServerErrorException::new);
            }

            return Mono.just(true);
        });
    }

    @Override
    public Mono<Boolean> deleteImage(final Path filePath) {
        return Mono.defer(() -> {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                return Mono.error(ServerErrorException::new);
            }

            return Mono.just(true);
        });
    }

    @Override
    public Mono<DataBuffer> getSubImage(final Path filePath, int x, int y, int width, int height) {
        return Mono.defer(() -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(Files.newInputStream(filePath));

                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage.getSubimage(x, y, width, height), IMAGE_FORMAT, byteArray);

                return Mono.just(new DefaultDataBufferFactory().wrap(byteArray.toByteArray()));
            } catch (IOException e) {
                return Mono.error(ServerErrorException::new);
            }
        });
    }

    @Override
    public Mono<Boolean> overrideImage(final Path sourceFile, BufferedImage fragment, int x, int y, int width, int height) {
        return Mono.defer(() -> {
            try {
                final BufferedImage source = ImageIO.read(Files.newInputStream(sourceFile));

                for (int i = y; i < source.getHeight() && i - y < height; ++i) {
                    for (int j = x; j < source.getWidth() && j - x < width; ++j) {
                        source.setRGB(j, i, fragment.getRGB(j - x, i - y));
                    }
                }

                ImageIO.write(source, IMAGE_FORMAT, new BufferedOutputStream(Files.newOutputStream(sourceFile)));

                return Mono.just(true);
            } catch (IOException e) {
                e.printStackTrace();
                return Mono.error(ServerErrorException::new);
            }
        });
    }

}
