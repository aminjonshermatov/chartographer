package com.shermatov.chartographer.runner;

import com.shermatov.chartographer.constants.ChartasConstants;
import com.shermatov.chartographer.repository.ChartasConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationConfigRunner implements ApplicationRunner {

    @Autowired
    ChartasConfigRepository chartasConfigRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String path;
        if (args.getNonOptionArgs().isEmpty()) path = ChartasConstants.PATH_TO_CONTENT_FOLDER;
        else path = args.getNonOptionArgs().get(0);

        chartasConfigRepository.setPathToContentFolder(path)
                .then()
                .doOnSuccess((__) -> { log.info("Path to content folder: {}", path); })
                .subscribe();
    }
}
