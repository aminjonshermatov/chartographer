package com.shermatov.chartographer.repository.impl;

import com.shermatov.chartographer.repository.ChartasConfigRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ChartasConfigRepositoryImpl implements ChartasConfigRepository {

    private String pathToContentFolder;

    @Override
    public Mono<String> getPathToContentFolder() {
        return Mono.just(pathToContentFolder);
    }

    @Override
    public Mono<Void> setPathToContentFolder(String pathToContentFolder) {
        this.pathToContentFolder = pathToContentFolder;
        return Mono.empty();
    }
}
