package com.shermatov.chartographer.repository.impl;

import com.shermatov.chartographer.domain.Charta;
import com.shermatov.chartographer.repository.ChartasRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class ChartasRepositoryImpl implements ChartasRepository {

    private final Map<String, Charta> chartasHashMap = new HashMap<>();

    @Override
    public Mono<Optional<Charta>> findById(String id) {
        return Mono.just(
                chartasHashMap.containsKey(id)
                        ? Optional.of(chartasHashMap.get(id))
                        : Optional.empty()
        );
    }

    @Override
    public Mono<Void> deleteById(String id) {
        chartasHashMap.remove(id);
        return Mono.empty();
    }

    @Override
    public Mono<String> insert(Charta charta) {
        chartasHashMap.put(charta.getId(), charta);
        return Mono.just(charta.getId());
    }
}
