package com.shermatov.chartographer.repository.impl;

import com.shermatov.chartographer.exception.ServerErrorException;
import com.shermatov.chartographer.repository.ImagesRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Repository
public class ImagesRepositoryImpl implements ImagesRepository {

    private static final String IMAGE_FORMAT = "bmp";
    private static final Color DEFAULT_COLOR = new Color(0, 0, 0);

    @Override
    public Mono<Void> createDefaultImage(final String folder, final String id, int width, int height) {
        return Mono.defer(() -> {
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();

            graphics2D.setColor(DEFAULT_COLOR);
            graphics2D.fillRect(0, 0, width, height);

            File directory = new File(folder);
            if (!directory.exists()) directory.mkdirs();

            String path = String.format("%s/%s.%s", folder, id, IMAGE_FORMAT);
            System.out.println(path);
            try {
                ImageIO.write(bufferedImage, IMAGE_FORMAT, new File(path));
            } catch (IOException e) {
                return Mono.error(new ServerErrorException());
            }

            return Mono.empty();
        });
    }

    @Override
    public Mono<BufferedImage> getImage(final String imagePath) {
        File image = new File(imagePath);

        if (!image.exists()) return Mono.error(ServerErrorException::new);

        return Mono.just(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
    }

    @Override
    public Mono<DataBuffer> overrideImage(Mono<Resource> image, int x, int y, int width, int height) {
        return null;
    }

}
