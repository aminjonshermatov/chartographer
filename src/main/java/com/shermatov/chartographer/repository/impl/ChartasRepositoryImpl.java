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
    public Mono<Charta> findById(String id) {
        if (!chartasHashMap.containsKey(id)) return Mono.empty();
        return Mono.just(chartasHashMap.get(id));
    }

    @Override
    public Mono<Boolean> deleteById(String id) {
        if (!chartasHashMap.containsKey(id)) return Mono.empty();
        chartasHashMap.remove(id);
        return Mono.just(true);
    }

    @Override
    public Mono<String> insert(Charta charta) {
        chartasHashMap.put(charta.getId(), charta);
        return Mono.just(charta.getId());
    }
}
