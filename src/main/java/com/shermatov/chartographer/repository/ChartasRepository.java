package com.shermatov.chartographer.repository;

import com.shermatov.chartographer.domain.Charta;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ChartasRepository {

    Mono<Optional<Charta>> findById(String id);
    Mono<Void> deleteById(String id);
    Mono<String> insert(Charta charta);

}
