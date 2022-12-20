package com.likelion.mutsasns.dto;

public class SuccessResponse<T> extends AbstractResultResponse<T> {
    public SuccessResponse(T result) {
        this("SUCCESS", result);
    }

    public SuccessResponse(String resultCode, T result) {
        super(resultCode, result);
    }
}
