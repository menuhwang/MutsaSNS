package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ErrorResponse;
import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.exception.AbstractBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.PersistenceException;

import static com.likelion.mutsasns.exception.ErrorCode.DATABASE_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ResultResponse<ErrorResponse>> persistenceException() {
        log.error("{} {}", DATABASE_ERROR.name(), DATABASE_ERROR.getMessage());
        return ResponseEntity.status(DATABASE_ERROR.getHttpStatus())
                .body(ResultResponse.error(ErrorResponse.of(DATABASE_ERROR)));
    }

    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<ResultResponse<ErrorResponse>> abstractBaseExceptionHandler(AbstractBaseException e) {
        log.error("{} {}", e.getErrorCode().name(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ResultResponse.error(e));
    }
}
