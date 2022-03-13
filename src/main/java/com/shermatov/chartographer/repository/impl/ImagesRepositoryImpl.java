package com.shermatov.chartographer.repository.impl;

import ch.qos.logback.core.util.FileUtil;
import com.shermatov.chartographer.exception.ServerErrorException;
import com.shermatov.chartographer.repository.ImagesRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.shermatov.chartographer.constants.ChartasConstants.DEFAULT_COLOR;
import static com.shermatov.chartographer.constants.ChartasConstants.IMAGE_FORMAT;

@Repository
public class ImagesRepositoryImpl implements ImagesRepository {

    @Override
    public Mono<Boolean> createDefaultImage(final String folder, final String id, int width, int height) {
        return Mono.defer(() -> {
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();

            graphics2D.setColor(DEFAULT_COLOR);
            graphics2D.fillRect(0, 0, width, height);

            File directory = new File(folder);
            if (!directory.exists()) directory.mkdirs();

            String path = String.format("%s/%s.%s", folder, id, IMAGE_FORMAT);
            try {
                ImageIO.write(bufferedImage, IMAGE_FORMAT, new File(path));
            } catch (IOException e) {
                return Mono.error(ServerErrorException::new);
            }

            return Mono.just(true);
        });
    }

    @Override
    public Mono<Boolean> deleteImage(String folder, String id) {
        return Mono.defer(() -> {
            File directory = new File(folder);
            // folder must exist, otherwise incorrect algorithm
            if (!directory.exists()) return Mono.error(ServerErrorException::new);

            Path filePath = Paths.get(String.format("%s/%s.%s", folder, id, IMAGE_FORMAT));
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                return Mono.error(ServerErrorException::new);
            }

            return Mono.just(true);
        });
    }

    @Override
    public Mono<DataBuffer> getSubImage(final String folder, final String id, int x, int y, int width, int height) {
        return Mono.defer(() -> {
            File directory = new File(folder);
            // folder must exist, otherwise incorrect algorithm
            if (!directory.exists()) return Mono.error(ServerErrorException::new);

            File imageFile = new File(String.format("%s/%s.%s", folder, id, IMAGE_FORMAT));
            if (!imageFile.exists()) return Mono.error(ServerErrorException::new);

            try {
                BufferedImage bufferedImage = ImageIO.read(imageFile);

                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage.getSubimage(x, y, width, height), IMAGE_FORMAT, byteArray);

                return Mono.just(new DefaultDataBufferFactory().wrap(byteArray.toByteArray()));
            } catch (IOException e) {
                return Mono.error(ServerErrorException::new);
            }
        });
    }

    @Override
    public Mono<DataBuffer> overrideImage(Mono<Resource> image, int x, int y, int width, int height) {
        return null;
    }

}
