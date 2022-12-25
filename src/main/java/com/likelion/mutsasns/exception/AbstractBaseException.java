package com.likelion.mutsasns.exception;

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
}
