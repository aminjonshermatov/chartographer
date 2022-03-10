package com.shermatov.chartographer.exception;

import org.springframework.http.HttpStatus;

public class ServerErrorException extends Exception {

    public static final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    public static final String errorMessage = "Internal server error";

}
