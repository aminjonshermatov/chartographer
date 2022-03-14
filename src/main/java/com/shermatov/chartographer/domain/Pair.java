package com.shermatov.chartographer.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Pair <T, U> {

    private final T first;
    private final U second;

}
