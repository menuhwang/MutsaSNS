package com.likelion.mutsasns.dto;

import com.likelion.mutsasns.exception.AbstractBaseException;
import lombok.Getter;

@Getter
public class ResultResponse<T> {
    private final String resultCode;
    private final T result;

    private ResultResponse(String resultCode, T result) {
        this.resultCode = resultCode;
        this.result = result;
    }

    public static  <T> ResultResponse<T> success(T result) {
        return new ResultResponse<>("SUCCESS", result);
    }

    public static ResultResponse<ErrorResponse> error(AbstractBaseException e) {
        return new ResultResponse<>("ERROR", ErrorResponse.of(e));
    }
}
