package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ErrorResponse;
import com.likelion.mutsasns.exception.AbstractBaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<ErrorResponse> abstractBaseExceptionHandler(AbstractBaseException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e));
    }
}
