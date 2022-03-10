package com.shermatov.chartographer.repository;

import com.shermatov.chartographer.domain.Charta;
import reactor.core.publisher.Mono;

public interface ChartasRepository {

    Mono<Charta> findById(String id);
    Mono<Void> deleteById(String id);
    Mono<String> insert(Charta charta);

}
