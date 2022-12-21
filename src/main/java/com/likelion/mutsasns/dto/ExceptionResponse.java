package com.likelion.mutsasns.dto;

import com.likelion.mutsasns.exception.AbstractBaseException;
import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final String errorCode;
    private final String message;

    public ExceptionResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ExceptionResponse(AbstractBaseException e) {
        this(e.getErrorCode().name(), e.getMessage());
    }
}
