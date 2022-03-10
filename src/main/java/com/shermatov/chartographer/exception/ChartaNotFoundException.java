package com.shermatov.chartographer.exception;

import org.springframework.http.HttpStatus;

public class ChartaNotFoundException extends Exception {

    public static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static final String errorMessage = "Charta not found";

}
