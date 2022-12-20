package com.likelion.mutsasns.dto;

import com.likelion.mutsasns.exception.AbstractBaseException;

public class ErrorResponse extends AbstractResultResponse<ErrorMessage> {
    public ErrorResponse(AbstractBaseException e) {
        super(e.getErrorCode().name(), new ErrorMessage(e.getMessage()));
    }
    public ErrorResponse(String resultCode, ErrorMessage errorMessage) {
        super(resultCode, errorMessage);
    }
}
class ErrorMessage {
    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
