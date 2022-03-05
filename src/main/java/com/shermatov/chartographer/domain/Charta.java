package com.shermatov.chartographer.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Charta {

    private String id;
    private Integer width;
    private Integer height;

}
