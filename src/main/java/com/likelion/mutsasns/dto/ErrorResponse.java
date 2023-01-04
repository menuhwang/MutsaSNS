package com.likelion.mutsasns.dto;


import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String errorCode;
    private final String message;

    private ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorResponse of(AbstractBaseException e) {
        return new ErrorResponse(e.getErrorCode().name(), e.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }
}
