package com.likelion.mutsasns.exception;

import com.likelion.mutsasns.dto.ErrorResponse;

public abstract class AbstractBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public AbstractBaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AbstractBaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public ErrorResponse errorResponse() {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }
}
