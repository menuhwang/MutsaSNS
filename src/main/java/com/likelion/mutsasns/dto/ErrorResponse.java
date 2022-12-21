package com.likelion.mutsasns.dto;

import com.likelion.mutsasns.exception.AbstractBaseException;

public class ErrorResponse extends AbstractResultResponse<ExceptionResponse> {
    public ErrorResponse(AbstractBaseException e) {
        super("ERROR", new ExceptionResponse(e));
    }
}
