package com.likelion.mutsasns.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractBaseException extends RuntimeException {
    private final HttpStatus httpStatus;

    public AbstractBaseException(HttpStatus errorStatus, String msg) {
        super(msg);
        this.httpStatus = errorStatus;
    }
}
