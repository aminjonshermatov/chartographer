package com.shermatov.chartographer.repository;

import reactor.core.publisher.Mono;

public interface ChartasConfigRepository {

    Mono<String> getPathToContentFolder();
    Mono<Void> setPathToContentFolder(String pathToContentFolder);

}
