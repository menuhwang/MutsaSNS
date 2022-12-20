package com.likelion.mutsasns.dto;

import lombok.Getter;

@Getter
public abstract class AbstractResultResponse<T> {
    private final String resultCode;
    private final T result;

    public AbstractResultResponse(String resultCode, T result) {
        this.resultCode = resultCode;
        this.result = result;
    }
}
